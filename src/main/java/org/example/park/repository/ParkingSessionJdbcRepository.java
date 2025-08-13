package org.example.park.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ParkingSessionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Сложный запрос: Получение статистики по парковочным сессиям с группировкой по дням
     * и расчетом среднего времени парковки и доходов
     */
    public List<Map<String, Object>> getDailyParkingStatistics() {
        String sql = """
            SELECT 
                DATE(entry_time) as parking_date,
                COUNT(*) as total_sessions,
                COUNT(CASE WHEN exit_time IS NOT NULL THEN 1 END) as completed_sessions,
                COUNT(CASE WHEN exit_time IS NULL THEN 1 END) as active_sessions,
                AVG(EXTRACT(EPOCH FROM (COALESCE(exit_time, CURRENT_TIMESTAMP) - entry_time))/3600) as avg_hours,
                SUM(COALESCE(total_cost, 0)) as total_revenue,
                AVG(COALESCE(total_cost, 0)) as avg_revenue_per_session
            FROM parking_session 
            WHERE entry_time >= CURRENT_DATE - INTERVAL '30 days'
            GROUP BY DATE(entry_time)
            ORDER BY parking_date DESC
            """;
        
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Сложный запрос: Поиск самых прибыльных парковочных мест с детальной статистикой
     */
    public List<Map<String, Object>> getMostProfitableParkingSpots(int limit) {
        String sql = """
            SELECT 
                ps.number as spot_number,
                ps.spot_type,
                ps.hourly_rate,
                COUNT(pses.id) as total_sessions,
                SUM(COALESCE(pses.total_cost, 0)) as total_revenue,
                AVG(COALESCE(pses.total_cost, 0)) as avg_revenue_per_session,
                AVG(EXTRACT(EPOCH FROM (COALESCE(pses.exit_time, CURRENT_TIMESTAMP) - pses.entry_time))/3600) as avg_duration_hours,
                ROUND(
                    (SUM(COALESCE(pses.total_cost, 0)) / 
                    NULLIF(SUM(EXTRACT(EPOCH FROM (COALESCE(pses.exit_time, CURRENT_TIMESTAMP) - pses.entry_time))/3600), 0)
                , 2) as revenue_per_hour
            FROM parking_spot ps
            LEFT JOIN parking_session pses ON ps.id = pses.spot_id
            WHERE pses.entry_time >= CURRENT_DATE - INTERVAL '90 days'
            GROUP BY ps.id, ps.number, ps.spot_type, ps.hourly_rate
            HAVING COUNT(pses.id) > 0
            ORDER BY total_revenue DESC, revenue_per_hour DESC
            LIMIT ?
            """;
        
        return jdbcTemplate.queryForList(sql, limit);
    }

    /**
     * Сложный запрос: Анализ пиковых часов загрузки парковки
     */
    public List<Map<String, Object>> getPeakHoursAnalysis() {
        String sql = """
            WITH hourly_stats AS (
                SELECT 
                    EXTRACT(HOUR FROM entry_time) as hour_of_day,
                    COUNT(*) as sessions_count,
                    AVG(EXTRACT(EPOCH FROM (COALESCE(exit_time, CURRENT_TIMESTAMP) - entry_time))/3600) as avg_duration,
                    SUM(COALESCE(total_cost, 0)) as hourly_revenue
                FROM parking_session
                WHERE entry_time >= CURRENT_DATE - INTERVAL '30 days'
                GROUP BY EXTRACT(HOUR FROM entry_time)
            ),
            hourly_ranking AS (
                SELECT 
                    hour_of_day,
                    sessions_count,
                    avg_duration,
                    hourly_revenue,
                    RANK() OVER (ORDER BY sessions_count DESC) as rank_by_sessions,
                    RANK() OVER (ORDER BY hourly_revenue DESC) as rank_by_revenue
                FROM hourly_stats
            )
            SELECT 
                hour_of_day,
                sessions_count,
                ROUND(avg_duration::numeric, 2) as avg_duration_hours,
                ROUND(hourly_revenue::numeric, 2) as hourly_revenue,
                rank_by_sessions,
                rank_by_revenue,
                CASE 
                    WHEN rank_by_sessions <= 3 THEN 'HIGH'
                    WHEN rank_by_sessions <= 6 THEN 'MEDIUM'
                    ELSE 'LOW'
                END as traffic_level
            FROM hourly_ranking
            ORDER BY hour_of_day
            """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Сложный запрос: Поиск подозрительных сессий (возможное мошенничество)
     */
    public List<Map<String, Object>> findSuspiciousSessions() {
        String sql = """
            SELECT 
                ps.id as session_id,
                ps.entry_time,
                ps.exit_time,
                ps.total_cost,
                v.license_plate,
                v.model,
                ps2.number as spot_number,
                u.username as operator_name,
                CASE 
                    WHEN ps.exit_time IS NULL AND ps.entry_time < CURRENT_TIMESTAMP - INTERVAL '24 hours' 
                    THEN 'LONG_ACTIVE_SESSION'
                    WHEN ps.total_cost > 1000 
                    THEN 'HIGH_COST_SESSION'
                    WHEN ps.entry_time > ps.exit_time 
                    THEN 'INVALID_TIMESTAMPS'
                    WHEN EXTRACT(EPOCH FROM (ps.exit_time - ps.entry_time))/3600 > 168 
                    THEN 'OVERWEEK_SESSION'
                    ELSE 'NORMAL'
                END as suspicion_type,
                EXTRACT(EPOCH FROM (COALESCE(ps.exit_time, CURRENT_TIMESTAMP) - ps.entry_time))/3600 as duration_hours
            FROM parking_session ps
            JOIN vehicle v ON ps.vehicle_id = v.id
            JOIN parking_spot ps2 ON ps.spot_id = ps2.id
            JOIN app_user u ON ps.operator_id = u.id
            WHERE 
                (ps.exit_time IS NULL AND ps.entry_time < CURRENT_TIMESTAMP - INTERVAL '24 hours') OR
                (ps.total_cost > 1000) OR
                (ps.entry_time > ps.exit_time) OR
                (ps.exit_time IS NOT NULL AND EXTRACT(EPOCH FROM (ps.exit_time - ps.entry_time))/3600 > 168)
            ORDER BY ps.entry_time DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Сложный запрос: Расчет эффективности операторов
     */
    public List<Map<String, Object>> getOperatorEfficiencyReport() {
        String sql = """
            WITH operator_stats AS (
                SELECT 
                    u.id as operator_id,
                    u.username as operator_name,
                    u.role as operator_role,
                    COUNT(ps.id) as total_sessions_handled,
                    COUNT(CASE WHEN ps.exit_time IS NOT NULL THEN 1 END) as completed_sessions,
                    COUNT(CASE WHEN ps.exit_time IS NULL THEN 1 END) as active_sessions,
                    SUM(COALESCE(ps.total_cost, 0)) as total_revenue_generated,
                    AVG(COALESCE(ps.total_cost, 0)) as avg_revenue_per_session,
                    AVG(EXTRACT(EPOCH FROM (COALESCE(ps.exit_time, CURRENT_TIMESTAMP) - ps.entry_time))/3600) as avg_session_duration,
                    MIN(ps.entry_time) as first_session_date,
                    MAX(ps.entry_time) as last_session_date
                FROM app_user u
                LEFT JOIN parking_session ps ON u.id = ps.operator_id
                WHERE u.role = 'OPERATOR'
                GROUP BY u.id, u.username, u.role
            )
            SELECT 
                operator_id,
                operator_name,
                operator_role,
                total_sessions_handled,
                completed_sessions,
                active_sessions,
                ROUND(total_revenue_generated::numeric, 2) as total_revenue,
                ROUND(avg_revenue_per_session::numeric, 2) as avg_revenue_per_session,
                ROUND(avg_session_duration::numeric, 2) as avg_duration_hours,
                ROUND(
                    (completed_sessions::float / NULLIF(total_sessions_handled, 0)) * 100, 2
                ) as completion_rate_percent,
                ROUND(
                    (total_revenue_generated / NULLIF(total_sessions_handled, 0)), 2
                ) as revenue_per_session,
                first_session_date,
                last_session_date
            FROM operator_stats
            ORDER BY total_revenue_generated DESC, completion_rate_percent DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Сложный запрос: Прогнозирование загрузки парковки на основе исторических данных
     */
    public List<Map<String, Object>> getParkingLoadForecast() {
        String sql = """
            WITH daily_patterns AS (
                SELECT 
                    EXTRACT(DOW FROM entry_time) as day_of_week,
                    EXTRACT(HOUR FROM entry_time) as hour_of_day,
                    COUNT(*) as historical_sessions,
                    AVG(EXTRACT(EPOCH FROM (COALESCE(exit_time, CURRENT_TIMESTAMP) - entry_time))/3600) as avg_duration,
                    STDDEV(EXTRACT(EPOCH FROM (COALESCE(exit_time, CURRENT_TIMESTAMP) - entry_time))/3600) as duration_stddev
                FROM parking_session
                WHERE entry_time >= CURRENT_DATE - INTERVAL '90 days'
                GROUP BY EXTRACT(DOW FROM entry_time), EXTRACT(HOUR FROM entry_time)
            ),
            current_week_stats AS (
                SELECT 
                    EXTRACT(DOW FROM entry_time) as day_of_week,
                    EXTRACT(HOUR FROM entry_time) as hour_of_day,
                    COUNT(*) as current_week_sessions
                FROM parking_session
                WHERE entry_time >= DATE_TRUNC('week', CURRENT_DATE)
                GROUP BY EXTRACT(DOW FROM entry_time), EXTRACT(HOUR FROM entry_time)
            )
            SELECT 
                dp.day_of_week,
                dp.hour_of_day,
                CASE dp.day_of_week
                    WHEN 0 THEN 'Sunday'
                    WHEN 1 THEN 'Monday'
                    WHEN 2 THEN 'Tuesday'
                    WHEN 3 THEN 'Wednesday'
                    WHEN 4 THEN 'Thursday'
                    WHEN 5 THEN 'Friday'
                    WHEN 6 THEN 'Saturday'
                END as day_name,
                dp.historical_sessions,
                COALESCE(cws.current_week_sessions, 0) as current_week_sessions,
                ROUND(dp.avg_duration::numeric, 2) as avg_duration_hours,
                ROUND(dp.duration_stddev::numeric, 2) as duration_stddev_hours,
                ROUND(
                    (dp.historical_sessions * 1.1)::numeric, 0
                ) as predicted_sessions_next_week,
                CASE 
                    WHEN dp.historical_sessions > 50 THEN 'HIGH_LOAD'
                    WHEN dp.historical_sessions > 25 THEN 'MEDIUM_LOAD'
                    ELSE 'LOW_LOAD'
                END as load_category
            FROM daily_patterns dp
            LEFT JOIN current_week_stats cws ON dp.day_of_week = cws.day_of_week 
                AND dp.hour_of_day = cws.hour_of_day
            ORDER BY dp.day_of_week, dp.hour_of_day
            """;

        return jdbcTemplate.queryForList(sql);
    }
}
