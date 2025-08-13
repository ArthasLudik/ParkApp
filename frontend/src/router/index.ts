import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ParkingSpotsView from '../views/ParkingSpotsView.vue'
import SessionsView from '../views/SessionsView.vue'
import UsersView from '../views/UsersView.vue'
import VehiclesView from '../views/VehiclesView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/spots',
      name: 'spots',
      component: ParkingSpotsView
    },
    {
      path: '/sessions',
      name: 'sessions',
      component: SessionsView
    },
    {
      path: '/users',
      name: 'users',
      component: UsersView
    },
    {
      path: '/vehicles',
      name: 'vehicles',
      component: VehiclesView
    }
  ]
})

export default router
