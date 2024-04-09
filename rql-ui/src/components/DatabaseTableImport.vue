<template>
  <div class="databasetableimport">
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
      </b-collapse>
    </b-navbar>
    <div class="row">
      <div class="col-lg-12">
        <form-wizard color="#424242" error-color="#ff4949" shape="tab" @on-complete="onComplete">
          <h2 slot="title">Data Import</h2>
          <tab-content title="Data preview" :before-change="validateFirstStep">
            <div v-if="!goNext && goToSecondStep">
              <b-alert variant="danger" show>
                No file selected! please select file until go next
              </b-alert>
            </div>
            <form v-on:submit.prevent="onSubmit">
              <div class="row">
                <div class="col-lg-4">
                  <div class="box" style="display:inline-block">
                    <input type="file" name="file" :accept="accept" id="file" class="inputfile inputfile-3" @change="handleFileChange" />
                    <label v-if="value" for="file">
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="17" viewBox="0 0 20 17">
                        <path d="M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z"
                        />
                      </svg>
                      <span>{{value.name}}&hellip;</span>
                    </label>
                    <label v-else for="file">
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="17" viewBox="0 0 20 17">
                        <path d="M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z"
                        />
                      </svg>
                      <span>Choose a file&hellip;</span>
                    </label>
                  </div>
                </div>
                <div class="col-lg-4">
                </div>
                <div class="col-lg-4" v-if="goNext">
                  <b-form-select v-model="selectedSheet" :options="sheetList" class="mb-3" />
                </div>
              </div>
            </form>
            <iframe width="0" height="0" border="0" name="dummyframe" id="dummyframe"></iframe>
            <b-card no-body="" v-if="goNext">
              <div id="div0">
                <b-table show-empty stacked="md" :items="rows" :fields="headerList" :current-page="currentPage" :per-page="perPage">
                </b-table>
              </div>
              <div class="row" v-if="results.length != 0" style="margin-top:1%">
                <div class="col-lg-1"></div>
                <div class="col-lg-5">
                  <b-input-group>
                    <b-pagination :total-rows="totalRows" :per-page="perPage" v-model="currentPage">
                    </b-pagination>
                  </b-input-group>
                </div>
                <div class="col-lg-5">
                  <label>This sheet contain {{nbRow}} rows</label>
                </div>
              </div>
            </b-card>
          </tab-content>
          <tab-content title="Choose table name and columns" :before-change="validateAsync">
            <div v-if="!loading">
              <b-alert variant="danger" show>
                {{ErrorMessage.bodyText}}
              </b-alert>
            </div>
            <div class="row" v-if="isloading" style="margin-bottom:2%">
              <div class="col-lg-2"></div>
              <div class="col-lg-8">
                <div class="wrapper2">
                    <div class="loader2">
                        <div></div>
                        <div></div>
                        <div></div>
                        <div></div>
                        <div></div>
                        <div></div>
                        <div></div>
                        <div></div>
                        <div></div>
                    </div>
                </div>
                </div>
                <div class="col-lg-2"></div>
              </div>
            <div class="container">
              <div class="row">
                <div class="col-lg-2">
                  <h4 style="margin:auto">Table name:</h4>
                </div>
                <div class="col-lg-3">
                  <b-form-input type="text" maxlength="30" v-model="tableName"></b-form-input>
                </div>
              </div>
              <VueDualList :options="options"></VueDualList>
            </div>
          </tab-content>
          <tab-content title="Summary">
            <div class="loader" v-if="loading &&  importResult.length == 0"></div>
            <div v-if="loading &&  importResult.length != 0">
              <div id="summary">
                <b-card no-body="">
                  <h4 slot="header">Importation summary</h4>
                  <b-list-group flush="">
                    <b-list-group-item>Table name:
                      <span>{{importResult.tableName}}</span>
                    </b-list-group-item>
                    <b-list-group-item>Columns:
                      <span>{{importResult.nbCol}}
                      </span>
                    </b-list-group-item>
                    <b-list-group-item>Rows:
                      <span>{{importResult.nbRow}}</span>
                    </b-list-group-item>
                  </b-list-group>
                  <b-card-body>
                    <a class="card-link" :href="link" download>Download the SQL import script</a>
                  </b-card-body>
                  <b-card-footer>RQL</b-card-footer>
                </b-card>
              </div>
            </div>
            <div class="alert alert-danger sqlError" v-if="!loading">
              <strong>SQL Statement error !</strong>
              <br>
              <label>Status: {{ErrorMessage.status}}</label>
              <br>
              <label>Status Text: {{ErrorMessage.statusText}}</label>
              <br>
              <label>Error: {{ErrorMessage.bodyText}}</label>
            </div>
          </tab-content>
          <template slot="footer" slot-scope="props">
               <div class="wizard-footer-right">
                 <wizard-button v-if="props.activeTabIndex == 1"@click.native="props.nextTab()" class="wizard-footer-right" :style="props.fillButtonStyle">Finish</wizard-button>
                 <wizard-button v-if="props.activeTabIndex == 0"@click.native="props.nextTab()" class="wizard-footer-right" :style="props.fillButtonStyle">Next</wizard-button>
                 <wizard-button v-if="props.isLastStep" @click.native="onComplete" class="wizard-footer-right finish-button" :style="props.fillButtonStyle">{{props.isLastStep ? 'Home' : 'Next'}}</wizard-button>
               </div>
             </template>
        </form-wizard>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from 'axios'
  import Multiselect from 'vue-multiselect'
  import VueDualList from './VueDualList.vue'
  import Utils from './UtilsMixins'
  class Header {
    constructor (key, label, sortable) {
      this.label = label
      this.key = key
      this.sortable = sortable
    }
  }
  class Column {
    constructor (id, name) {
      this.id = id
      this.name = name
    }
  }
  export default {
    mixins: [Utils],
    name: 'databasetableimport',
    components: {
      axios,
      VueDualList,
      Multiselect
    },
    data () {
      return {
        value: null,
        accept: ['.xlsx, .xls'],
        link: '',
        results: [],
        importResult: [],
        selectedSheet: '',
        tableName: '',
        goNext: false,
        goNext2: false,
        headerList: [],
        goToSecondStep: false,
        colList: [],
        ListOfHeadList: [],
        ListOfColList: [],
        sheetList: [],
        currentPage: 1,
        ErrorMessage: '',
        perPage: 10,
        fileID: '',
        loading: true,
        isloading: false,
        totalRows: 0,
        project: {},
        username: localStorage.getItem('USERNAME'),
        projectname: localStorage.getItem('PROJECTNAME'),
        nbRow: 0,
        listID: [],
        pageOptions: [5, 10],
        rows: [],
        options: {
          label: 'Select the column list',
          inputOptions: { uppercase: true, isRequired: false },
          buttonOption: { textLeft: 'Select All', textRight: 'Unselect All' },
          resizeBox: 'md',
          items: [],
          colorItems: '#000000',
          selectedItems: []
        }
      }
    },
    computed: {},
    methods: {
      goToProject () {
        localStorage.removeItem('PROJECTID')
        this.$router.push('/project')
      },
      getProjectByID () {
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('project/{id}', { params: { id: projectID } }).then((response) => {
          this.project = response.data
        }, (response) => {

        })
      },
      isLastStep () {
        if (this.$refs.wizard) {
          return this.$refs.wizard.isLastStep
        }
        return false
      },
      signout () {
        localStorage.removeItem('TOKEN')
        localStorage.removeItem('USERNAME')
        localStorage.removeItem('PROJECTID')
        this.$router.push('/login')
      },
      onComplete () {
        this.$router.push('/workspace')
      },
      validateFirstStep () {
        this.goToSecondStep = true
        return (this.goNext)
      },
      handleFileChange (e) {
        this.goToSecondStep = false
        this.options.items = []
        this.options.selectedItems = []
        this.value = e.target.files[0]
        this.goNext = false
        this.results = []
        this.selectedSheet = ''
        this.ListOfHeadList = []
        this.ListOfColList = []
        this.sheetList = []
        let formData = new FormData()
        formData.append('file', this.value)
        this.headerList = []
        var token = localStorage.getItem('TOKEN')
        axios.post('server/dbimport/uploadXlsx', formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data',
              'Authorization': token
            }
          }).then((response) => {
            this.results = response.data
            this.rows = this.results.sheetList[0].rows
            this.tableName = this.results.sheetList[0].tableName
            this.selectedSheet = this.tableName
            this.fileID = this.results.fileID
            this.totalRows = this.results.sheetList[0].rows.length
            this.nbRow = this.results.sheetList[0].nbRow
            this.goNext = response.data.goNext
            for (var sheet of this.results.sheetList) {
              this.sheetList.push(sheet.tableName)
              let cpt = 0
              this.headerList = []
              this.colList = []
              for (var element of sheet.header) {
                this.headerList.push(new Header(element, element, true))
                if (element.localeCompare('')) {
                  this.colList.push(new Column(cpt, element))
                } else {
                }
                cpt++
              }
              this.ListOfColList.push(this.colList)
              this.ListOfHeadList.push(this.headerList)
            }
            this.headerList = this.ListOfHeadList[0].slice(0)
            this.options.items = this.ListOfColList[0].slice(0)
          }, (response) => {
            console.log('NOOOOOOO!!')
          })
      },
      validateAsync () {
        return new Promise((resolve, reject) => {
          this.loading = true
          this.isloading = true
          this.goNext2 = false
          this.importResult = []
          this.link = 'server/dbimport/' + this.fileID + '/scriptdownload'
          this.listID = JSON.stringify(this.options.selectedItems.map(a => a.id))
          var token = localStorage.getItem('TOKEN')
          var projectID = localStorage.getItem('PROJECTID')
          this.$http.get('dbimport/{id}/tableImport', { params: { id: this.fileID, sheetIndex: this.sheetList.indexOf(this.selectedSheet), tableName: this.tableName, projectID: projectID, columnList: this.listID } }, { headers: { 'Authorization': token } }).then((response) => {
            this.importResult = response.data
            this.goNext2 = this.importResult.goNext2
            this.isloading = false
            resolve(this.goNext2)
          }, (response) => {
            this.loading = false
            this.isloading = false
            this.ErrorMessage = response
            // eslint-disable-next-line
            reject('This is a custom validation error message. Click next again to get rid of the validation')
          })
        })
      },
      onSubmit () {
      }
    },
    mounted () {
      this.getProjectByID()
    },
    watch: {
      selectedSheet () {
        if (this.selectedSheet.length !== 0) {
          this.tableName = this.selectedSheet
          this.options.selectedItems = []
          this.importResult = []
          console.log(this.sheetList.indexOf(this.tableName))
          this.options.items = this.ListOfColList[this.sheetList.indexOf(this.tableName)].slice(0)
          this.headerList = this.ListOfHeadList[this.sheetList.indexOf(this.tableName)].slice(0)
          this.rows = this.results.sheetList[this.sheetList.indexOf(this.tableName)].rows.slice(0)
          this.totalRows = this.results.sheetList[this.sheetList.indexOf(this.tableName)].rows.length
          this.nbRow = this.results.sheetList[this.sheetList.indexOf(this.tableName)].nbRow
        }
      }
    }

  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .inputfile {
    width: 0.1px;
    height: 0.1px;
    opacity: 0;
    overflow: hidden;
    position: absolute;
    z-index: -1;
  }

  #div0 {
    overflow: auto
  }

  .inputfile+label {
    max-width: 100%;
    font-size: 1.25rem;
    /* 20px */
    font-weight: 700;
    text-overflow: unset;
    white-space: nowrap;
    cursor: pointer;
    display: inline-block;
    overflow: hidden;
    padding: 0.625rem 1.25rem;
    /* 10px 20px */
  }

  .no-js .inputfile+label {
    display: none;
  }

  .inputfile:focus+label,
  .inputfile.has-focus+label {
    outline: 1px dotted #424242;
    outline: -webkit-focus-ring-color auto 5px;
  }

  .inputfile+label * {
    /* pointer-events: none; */
    /* in case of FastClick lib use */
  }

  .inputfile+label svg {
    width: 1em;
    height: 1em;
    vertical-align: middle;
    fill: currentColor;
    margin-top: -0.25em;
    /* 4px */
    margin-right: 0.25em;
    /* 4px */
  }

  .inputfile-3+label {
    color: #424242;
  }

  .inputfile-3:focus+label,
  .inputfile-3.has-focus+label,
  .inputfile-3+label:hover {
    color: #424242;
  }
  .databasetableimport  .wrapper2 {
        width: 100%;
        height: 2px;
        left: 0;
        right: 0;
        bottom: 0;
        top: 0;
        margin-top: 2%;
        margin-bottom: 0%;
    }

    .databasetableimport .loader2 {
        height: 100%;
        display: flex;
        -webkit-transform: translateZ(0);
        transform: translateZ(0);
    }

    .databasetableimport .loader2 div {
        flex: 1;
        background: #009688;
        -webkit-animation: go 0.8s infinite alternate ease;
        animation: go 0.8s infinite alternate ease;
        box-shadow: 0 0 20px #009688;
    }

    .loader2 div:nth-child(1) {
        -webkit-animation-delay: -0.72s;
        animation-delay: -0.72s;
    }

    .loader2 div:nth-child(2) {
        -webkit-animation-delay: -0.64s;
        animation-delay: -0.64s;
    }

    .loader2 div:nth-child(3) {
        -webkit-animation-delay: -0.56s;
        animation-delay: -0.56s;
    }

    .loader2 div:nth-child(4) {
        -webkit-animation-delay: -0.48s;
        animation-delay: -0.48s;
    }

    .loader2 div:nth-child(5) {
        -webkit-animation-delay: -0.4s;
        animation-delay: -0.4s;
    }

    .loader2 div:nth-child(6) {
        -webkit-animation-delay: -0.32s;
        animation-delay: -0.32s;
    }

    .loader2 div:nth-child(7) {
        -webkit-animation-delay: -0.24s;
        animation-delay: -0.24s;
    }

    .loader2 div:nth-child(8) {
        -webkit-animation-delay: -0.16s;
        animation-delay: -0.16s;
    }

    .loader2 div:nth-child(9) {
        -webkit-animation-delay: -0.08s;
        animation-delay: -0.08s;
    }

    .loader2 div:nth-child(10) {
        -webkit-animation-delay: 0s;
        animation-delay: 0s;
    }

    @-webkit-keyframes go {
        100% {
            background: transparent;
            flex: 10;
            box-shadow: 0 0 0 transparent;
        }
    }
</style>
