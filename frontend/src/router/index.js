import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Human from '../views/Human.vue'
import Simulation from '../views/Simulation.vue'
import Indicator from '../views/Indicator.vue'
import Family from '../views/Family.vue'
import Event from '../views/Event.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/human',
    name: 'Human',
    component: Human
  },
  {
    path: '/simulation',
    name: 'Simulation',
    component: Simulation
  },
  {
    path: '/indicator',
    name: 'Indicator',
    component: Indicator
  },
  {
    path: '/family',
    name: 'Family',
    component: Family
  },
  {
    path: '/event',
    name: 'Event',
    component: Event
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
