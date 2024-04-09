<template id="workspace">

  <Body class="workspace">
    <div>
      <b-navbar class="navbar" toggleable="md" type="dark" variant="info">
        <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
        <b-navbar-brand href="#">RULES QUERY LANGUAGE</b-navbar-brand>
        <b-collapse is-nav id="nav_collapse">
          <b-navbar-nav>
            <b-nav-item href="workspace">HOME</b-nav-item>
          </b-navbar-nav>
          <!-- Right aligned nav items -->
          <b-navbar-nav class="ml-auto">
            <b-nav-item-dropdown right>
              <!-- Using button-content slot -->
              <template slot="button-content">
                <em>{{username}}</em>
              </template>
              <b-dropdown-item @click="signout" href="">Signout</b-dropdown-item>
            </b-nav-item-dropdown>
            <b-nav-item @click="goToProject" href="#">{{projectname}} <i class="fa fa-sign-out"></i></b-nav-item>
          </b-navbar-nav>
        </b-collapse>
      </b-navbar>
      <div>
        <div class="row no-margin-top">
          <div class="col-lg-2 dbs">
            <b-card-header header-tag="header" class="p-1" role="tab">
              <b-btn block @click="fileImport" size="sm" variant="dark">Import data</b-btn>
            </b-card-header>
            <DataBaseSchema ref="DataBaseSchema"></DataBaseSchema>
          </div>
          <div class="col-lg-10">
            <QueryArea v-on:querySend="querySend" v-bind:constructedquery="constructedquery" ref="queryArea"></QueryArea>
            <div v-if="load == true" class="row">
              <div class="col-lg-8">
                <h2>Submition...</h2>
                <div id="loader" class="loader"></div>
              </div>
            </div>
            <div v-if="showModal" class="row model ">
              <div class="col-lg-9">
                <div>
                  <h3>RQL QUERY SUBMMITED</h3>
                </div>
                <div>
                  <div class="row">
                    <h5>Agree set computation SQL query:</h5>
                    <textarea disabled class="txtdis form-control model2" rows="6" v-model='rqlParsed'></textarea>
                  </div>
                  <div class="row">
                    <h5>What do you want?</h5>
                  </div>
                  <div class="row">
                    <div class="col-lg-2">
                    </div>
                    <b-btn variant="info" class="btn btnfont15  btn-primary btn-block col-lg-3" v-on:click="GenerateAllRules">Generate all rules</b-btn>
                    <div class="col-lg-2">
                    </div>
                    <b-btn variant="info" class="btn btnfont15  btn-primary btn-block col-lg-3" v-on:click="CheckRule">Check a single rule</b-btn>
                    <div class="col-lg-2">
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <RulesArea class="no-margin-top" v-bind:isdisplay="display1" v-bind:pquery="query1"></RulesArea>
            <SqlArea class="no-margin-top" v-bind:isdisplay="display2" v-bind:queryID="query2" @refreshData="refreshData"></SqlArea>
            <CheckRule class="no-margin-top" v-bind:isdisplay="display3" :options="options" v-bind:queryID="queryID2"></CheckRule>
            <div class="row">
              <div v-if="!loading" class="alert alert-danger col-lg-7 rqlError">
                <strong>SQL Statement error !</strong>
                <br>
                <label>Status: {{ErrorMessage.status}}</label>
                <br>
                <label>Status Text: {{ErrorMessage.statusText}}</label>
                <br>
                <label>Error: {{ErrorMessage.bodyText}}</label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Body>
</template>

<script>
  import Vue from 'vue'
  import CheckRule from './CheckRule'
  import QueryArea from './QueryArea'
  import SqlArea from './SqlArea'
  import RulesArea from './RulesArea'
  import DataBaseSchema from './DataBaseSchema'
  import vSelect from 'vue-select'
  import Utils from './UtilsMixins'
  Vue.component('v-select', vSelect)
  export default {
    mixins: [Utils],
    components: {
      QueryArea,
      RulesArea,
      SqlArea,
      CheckRule,
      DataBaseSchema,
      vSelect
    },
    data () {
      return {
        options: [],
        selected: [],
        query1: '',
        queryID: '',
        queryID2: '',
        rqlParsed: '',
        query2: '',
        query3: '',
        query4: '',
        isRqlQuery: '',
        showModal: false,
        display1: false,
        display2: false,
        display3: false,
        results: [],
        leftattributes: '',
        loading: true,
        ErrorMessage: '',
        load: false,
        project: {},
        mode: 0,
        constructedquery: '',
        username: localStorage.getItem('USERNAME'),
        projectname: localStorage.getItem('PROJECTNAME')
      }
    },
    watch: {
      selected () {
        for (var select in this.selected) {
          this.leftattributes = this.leftattributes + '' + select
        }
      }
    },
    mounted () {
      this.getProjectByID()
    },
    methods: {
      refreshData (ref) {
        this.$refs.queryArea.getTableHeader()
        this.$refs.DataBaseSchema.refreshData()
      },
      signout () {
        localStorage.removeItem('TOKEN')
        localStorage.removeItem('USERNAME')
        localStorage.removeItem('PROJECTID')
        this.$router.push('/login')
      },
      getProjectByID () {
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('project/{id}', { params: { id: projectID } }).then((response) => {
          this.project = response.data
        }, (response) => {

        })
      },
      fileImport () {
        this.$router.push('/databaseTableImport')
      },
      goToProject () {
        localStorage.removeItem('PROJECTID')
        this.$router.push('/project')
      },
      onLoadError (response) {
        if (response.status === 401) {
          localStorage.removeItem('TOKEN')
        } else {
          console.error('Server problem', response)
        }
        this.$router.push('Login')
      },
      querySend (query) {
        this.showModal = false
        this.load = true
        this.loading = true
        this.display2 = false
        this.display3 = false
        this.display1 = false
        var projectID = localStorage.getItem('PROJECTID')
        let formData = new FormData()
        formData.append('query', query)
        formData.append('projectID', projectID)
        if (this.query3.localeCompare(query) || query === '') {
          this.$http.post('query/typequery', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }).then((response) => {
            this.isRqlQuery = response.data.type
            if (this.isRqlQuery === 'RQL') {
              this.queryID = response.data.queryKey
              this.options = response.data.attributesList
              this.rqlParsed = response.data.ruleParsed
              this.query3 = query
              this.load = false
              this.showModal = true
              this.display2 = false
              this.display3 = false
            } else {
              this.query2 = response.data.queryKey
              this.load = false
              this.display2 = true
              this.display1 = false
              this.display3 = false
            }
          }, (response) => {
            this.ErrorMessage = response
            this.load = false
            this.loading = false
          })
        } else {
          this.load = false
          this.query3 = query
          this.showModal = true
          this.display2 = false
          this.display3 = false
        }
      },
      constructedquerysend (constructedquery) {
        this.mode = 0
        this.constructedquery = constructedquery
      },
      HideModal () {
        this.showModal = false
        this.load = false
      },
      GenerateAllRules () {
        this.showModal = false
        this.display1 = true
        this.query1 = this.queryID
      },
      CheckRule () {
        this.showModal = false
        this.display3 = true
        this.display2 = false
        this.display1 = false
        this.queryID2 = this.queryID
      },
      switchAssisted () {
        this.mode = 1
      },
      switchSpecialist () {
        this.mode = 0
      }
    }
  }
</script>
<style>
  .workspace #navbarResponsive {
    display: inline-block;
  }

  .workspace #navref {
    display: inline-block;
  }

  body {
    background: #FAFAFA !important;
  }

  .workspace .dbs {
    width: 100%;
    padding-top: 0.8%;
    padding-left: 2%;
  }

  .workspace .iconsNavbar {
    color: #fff;
    margin-right: 5px
  }

  .workspace .model {
    margin-top: 1%;
  }

  .workspace .model2 {
    margin-left: 1%;
    margin-right: 1%
  }

  .workspace .model h3 {
    margin-top: 0;
    margin-bottom: 0%;
    color: #2397e4;
  }

  .workspace .text-right {
    text-align: right;
  }

  .workspace .txtdis {
    margin-top: 0%;
    width: 100%;
  }

  .workspace textarea {
    resize: none;
  }

  .workspace h5 {
    font-size: 13pt;
    padding-left: 0%;
    margin-top: 1%;

    margin-bottom: 1%;
    margin-left: 1%;

  }

  .workspace .btnfont15 {
    font-size: 15pt
  }

  .workspace .btnfont12 {
    font-size: 12pt
  }

  .workspace #loader {
    margin-top: 5%
  }

  .workspace .row {
    margin-top: 1%
  }

  .workspace .no-margin-top {
    margin-top: 0%
  }
</style>
