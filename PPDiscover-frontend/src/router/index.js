import { createRouter, createWebHistory } from 'vue-router'
import SearchPage from '../views/SearchPage.vue'
import UploadPage from '../views/UploadPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/search'
    },
    {
      path: '/search',
      name: 'search',
      component: SearchPage
    },
    {
      path: '/upload',
      name: 'upload',
      component: UploadPage
    }
  ]
})

export default router 