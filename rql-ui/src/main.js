// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import BootstrapVue from 'bootstrap-vue/dist/bootstrap-vue.esm'
import App from './App'
import router from './router'
import VueResource from 'vue-resource'
import vSelect from 'vue-select'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'semantic-ui-css/semantic.min.css'
import VueCarousel from 'vue-carousel'
import VueClip from 'vue-clip'
import VueFormWizard from 'vue-form-wizard'
import 'vue-form-wizard/dist/vue-form-wizard.min.css'

require('./assets/RQL.css')
Vue.use(VueResource)
Vue.http.options.emulateJSON = true
Vue.use(BootstrapVue)
Vue.use(VueCarousel)
Vue.use(VueClip)
Vue.use(VueFormWizard)
Vue.component('v-select', vSelect)
window.bus = new Vue()
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
