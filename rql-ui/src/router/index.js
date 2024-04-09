import Vue from 'vue'
import Router from 'vue-router'

import Workspace from '@/components/Workspace'
import DatabaseTableImport from '@/components/DatabaseTableImport'
import Login from '@/components/Login'
import Project from '@/components/Project'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  base: '/rql',
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/workspace',
      name: 'Workspace',
      component: Workspace
    },
    {
      path: '/project',
      name: 'Project',
      component: Project
    },
    {
      path: '/DatabaseTableImport',
      name: 'DatabaseTableImport',
      component: DatabaseTableImport
    },
    {
      path: '/*',
      redirect: { name: 'Login' }
    }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
  }
  var token = localStorage.getItem('TOKEN')
  var projectID = localStorage.getItem('PROJECTID')
  if (token !== null) {
    next()
  } else {
    next({path: '/login'})
  }
  if (projectID !== null) {
    next()
  } else if (from.path !== '/project') {
    next({path: '/project'})
  } else {
    next()
  }
})

export default router
