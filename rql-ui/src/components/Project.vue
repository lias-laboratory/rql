<template id="project">
    <div class="project">
        <b-navbar class="navbar" toggleable="md" type="dark" variant="info">
            <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
            <b-navbar-brand href="#">RULES QUERY LANGUAGE</b-navbar-brand>
            <b-collapse is-nav id="nav_collapse">
                <b-navbar-nav>
                    <b-nav-item href="project">PROJECTS</b-nav-item>
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
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="row" style="margin-bottom:1%; margin-top:2%">
                                <div class="col-lg-6">
                                    <p style=" font-size: 14pt">Select your project:</p>
                                </div>
                                <div class="col-lg-6 inputText">
                                    <form>
                                        <input type="text" name="search" v-model="search" placeholder="Search..">
                                    </form>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-3"></div>
                        <div class="col-lg-3" v-if="select">
                            <input type="create" @click="select = !select" value="Create a new project" readonly="readonly">
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="center">
                        <div class="row">
                            <div class="col-lg-6" id="myrow">
                                <div class="row">
                                    <b-card class="column b-card" v-if="!isGridView" v-for="project in filteredTables" :key="project.id" @click="elementClicked(project)">
                                        <div class="row">
                                            <div class="col-lg-1">
                                                <i v-if="username == project.userid" class="fa fa-star"></i>
                                            </div>
                                            <div class="col-lg-6">
                                                <label>
                                                    <span class="span-style">Project name: </span>
                                                    <span style="font-size: 9pt ; font-weight: bold">{{project.name}}</span>
                                                </label>
                                            </div>
                                            <div class="col-lg-5">
                                                <label>
                                                    <span class="span-style">Creation date: </span>
                                                    <span style="font-size: 8pt ; font-weight: bold">{{project.creationDate}}</span>
                                                </label>
                                            </div>
                                        </div>
                                    </b-card>
                                </div>
                            </div>
                            <div class="col-lg-6">
                                <b-card title="Project Details" v-if="select">
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Name</p>
                                        </div>
                                        <div class="col-lg-9 inputText">
                                            <input :disabled="!(username == selectedProject.userid)" type="text" v-model="selectedProject.name">
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Creation date</p>
                                        </div>
                                        <div class="col-lg-9 inputText">
                                            <input type="text" v-model="selectedProject.creationDate" disabled>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Owner</p>
                                        </div>
                                        <div class="col-lg-9 inputText">
                                            <input type="text" v-model="selectedProject.userid" disabled>
                                        </div>
                                    </div>
                                    <div style="margin-top:2%" class="row" v-if="(username == selectedProject.userid)">
                                        <div class="col-lg-3">
                                            <p>Collaborators</p>
                                        </div>
                                        <div class="col-lg-9">
                                            <div id="multiselect">
                                                <multiselect v-model="collaborators" :options="optionss" :multiple="true" :custom-label="customLabel" track-by="username"
                                                    placeholder="Select your collaborators">
                                                </multiselect>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Description</p>
                                        </div>
                                        <div class="col-lg-9">
                                            <textarea :disabled="!(username == selectedProject.userid)" name="" id="" v-model="selectedProject.description" rows="7"
                                                style="width:100%; margin-top:2%"></textarea>
                                        </div>
                                    </div>
                                    <div class="row" id="buttongroup">
                                        <b-button-group style="width:100%">
                                            <b-button v-if="username == selectedProject.userid" @click="deleteProject" variant="danger" class="form-control">Delete</b-button>
                                            <b-button v-if="username == selectedProject.userid" @click="update" variant="primary" class="form-control">Update</b-button>
                                            <b-button variant="info" @click="selection" class="form-control">Open</b-button>
                                        </b-button-group>
                                    </div>
                                    <b-alert style="margin-bottom:0%; margin-top:2%" :show="dismissCountDown2" dismissible variant="info" @dismissed="dismissCountDown2=0"
                                        @dismiss-count-down="countDownChanged2">
                                        <p>Operation executed with success</p>
                                    </b-alert>
                                    <b-alert style="margin-bottom:0%; margin-top:2%" :show="dismissCountDown4" dismissible variant="danger" @dismissed="dismissCountDown4=0"
                                        @dismiss-count-down="countDownChanged4">
                                        <p>{{errorMessage}}</p>
                                    </b-alert>
                                </b-card>
                                <b-card id="content" title="Create new project:" v-if="!select">
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Name</p>
                                        </div>
                                        <div class="col-lg-9 inputText">
                                            <input type="text" v-model="Project.name">
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Database type</p>
                                        </div>
                                        <div class="col-lg-9">
                                            <b-form-select id="myselect" v-model="Project.dbType" :options="options" class="mb-3" />
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Description</p>
                                        </div>
                                        <div class="col-lg-9">
                                            <textarea name="" id="" v-model="Project.description" rows="7" style="width:100%; margin-top:2%"></textarea>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-3">
                                            <p>Collaborators</p>
                                        </div>
                                        <div class="col-lg-9">
                                            <div id="multiselect">
                                                <multiselect v-model="ListItems" :options="optionss" :multiple="true" :custom-label="customLabel" track-by="username" placeholder="Select your collaborators">
                                                </multiselect>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-lg-7">
                                        </div>
                                        <div class="col-lg-5">
                                            <b-btn variant="info" @click="create" class="form-control">Create and Open</b-btn>
                                        </div>
                                    </div>
                                    <div id="contentchild" class="row" v-if="loading">
                                        <div class="col-lg-1"></div>
                                        <div class="col-lg-10">
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
                                        <div class="col-lg-1"></div>
                                    </div>
                                    <b-alert style="margin-bottom:0%; margin-top:2%" :show="dismissCountDown" dismissible variant="info" @dismissed="dismissCountDown=0"
                                        @dismiss-count-down="countDownChanged">
                                        <p>Your project Has been created</p>
                                    </b-alert>
                                    <b-alert style="margin-bottom:0%; margin-top:2%" :show="dismissCountDown3" dismissible variant="danger" @dismissed="dismissCountDown3=0"
                                        @dismiss-count-down="countDownChanged3">
                                        <p>{{errorMessage}}</p>
                                    </b-alert>
                                </b-card>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import Multiselect from 'vue-multiselect'
    import Utils from './UtilsMixins'
    export default {
      mixins: [Utils],
      name: 'project',
      components: {
        Multiselect
      },
      data () {
        return {
          dismissSecs: 10,
          dismissCountDown: 0,
          dismissCountDown2: 0,
          dismissCountDown3: 0,
          dismissCountDown4: 0,
          projectList: [],
          selectedItems: [],
          selectedId: '',
          selectedProject: {},
          search: '',
          select: false,
          isGridView: false,
          errorMessage: '',
          loading: false,
          collaborators: [],
          collaboratorsCurrent: [],
          optionss: [],
          options: ['POSTGRESQL'],
          Project: {
            id: '',
            name: '',
            description: '',
            creationDate: '',
            dbType: 'POSTGRESQL',
            userid: localStorage.getItem('USERNAME'),
            collaborators: []
          },
          username: localStorage.getItem('USERNAME'),
          ListItems: []
        }
      },
      methods: {
        arr_diff (a1, a2) {
          var a = []
          var diff = []
          for (var i = 0; i < a1.length; i++) {
            a[a1[i].username] = true
          }
          for (var f = 0; f < a2.length; f++) {
            if (a[a2[f].username]) {
              delete a[a2[f].username]
            }
          }
          for (var k in a) {
            diff.push(k)
          }
          return diff
        },
        customLabel (option) {
          return `${option.username} - ${option.lastName} ${option.firstName}`
        },
        signout () {
          localStorage.removeItem('TOKEN')
          localStorage.removeItem('USERNAME')
          localStorage.removeItem('PROJECTID')
          localStorage.removeItem('PROJECTNAME')
          this.$router.push('/login')
        },
        deleteProject () {
          this.$http.delete('project/{id}', { params: { id: this.selectedProject.id } }).then((response) => {
            this.showAlert2()
            this.selectedProject = {}
            this.loadProject()
          }, (response) => {
            this.errorMessage = response.bodyText
            this.showAlert4()
          })
        },
        sendToTrash (collaborator) {
          var i = this.collaborators.indexOf(collaborator)
          if (i !== -1) {
            this.collaborators.splice(i, 1)
          }
          this.collaboratorsTrash.push(collaborator)
        },
        deleteCollaborator (collaborator) {
          this.$http.delete('user/collaborator', { params: { projectid: this.selectedProject.id, username: collaborator } }).then((response) => {
          }, (response) => {
          })
        },
        newCollaborator (collaborator) {
          let formData = new FormData()
          formData.append('projectid', this.selectedProject.id)
          formData.append('username', collaborator)
          this.$http.post('user/collaborator', formData).then((response) => {
          }, (response) => {
          })
        },
        getAllUser () {
          this.$http.get('user', { params: { id: localStorage.getItem('USERNAME') } }).then((response) => {
            this.optionss = response.data
          }, (response) => {
          })
        },
        isValidProject () {
          return this.Project.name.localeCompare('')
        },
        selection () {
          localStorage.setItem('PROJECTID', this.selectedProject.id)
          localStorage.setItem('PROJECTNAME', this.selectedProject.name)
          this.$router.push('/workspace')
        },
        showAlert () {
          this.dismissCountDown = this.dismissSecs
        },
        showAlert2 () {
          this.dismissCountDown2 = this.dismissSecs
        },
        showAlert3 () {
          this.dismissCountDown3 = this.dismissSecs
        },
        showAlert4 () {
          this.dismissCountDown4 = this.dismissSecs
        },
        countDownChanged (dismissCountDown) {
          this.dismissCountDown = dismissCountDown
        },
        countDownChanged2 (dismissCountDown) {
          this.dismissCountDown2 = dismissCountDown
        },
        countDownChanged3 (dismissCountDown) {
          this.dismissCountDown3 = dismissCountDown
        },
        countDownChanged4 (dismissCountDown) {
          this.dismissCountDown4 = dismissCountDown
        },
        loadProject () {
          this.$http.get('project/allproject', { params: { userID: this.username } }).then((response) => {
            this.projectList = response.data
          }, (response) => {
          })
        },
        create () {
          if (this.isValidProject()) {
            this.loading = true
            this.Project.collaborators = this.ListItems
            this.$http.post('project/create', JSON.stringify(this.Project)).then((response) => {
              localStorage.setItem('PROJECTID', response.data)
              localStorage.setItem('PROJECTNAME', this.Project.name)
              this.loading = false
              this.showAlert()
              this.$router.push('/workspace')
            }, (response) => {
              this.loading = false
              this.errorMessage = response.bodyText
              this.showAlert3()
            })
          } else {
            this.errorMessage = 'Project name is required'
            this.showAlert3()
          }
        },
        update () {
          var trach = this.arr_diff(this.selectedProject.collaborators, this.collaborators)
          for (var i = 0; i < trach.length; i++) {
            this.deleteCollaborator(trach[i])
          }
          var news = this.arr_diff(this.collaborators, this.selectedProject.collaborators)
          for (var f = 0; f < news.length; f++) {
            this.newCollaborator(news[f])
          }
          this.selectedProject.collaborators = this.collaborators
          this.$http.post('project/update', JSON.stringify(this.selectedProject)).then((response) => {
            this.showAlert2()
            this.loadProject()
          }, (response) => {
            this.errorMessage = response.bodyText
            this.showAlert4()
          })
        },
        elementClicked (project) {
          this.selectedProject = project
          this.collaborators = project.collaborators
          this.select = true
        },
        createProject () {
          this.$router.push('/newproject')
        }
      },
      mounted () {
        this.loadProject()
        this.getAllUser()
      },
      computed: {
        filteredTables () {
          var self = this
          return this.projectList.filter(function (project) { return (project.name.toLowerCase().indexOf(self.search.toLowerCase()) >= 0 || project.creationDate.toLowerCase().indexOf(self.search.toLowerCase()) >= 0) })
        }
      },
      watch: {
      }
    }
</script>

<style>
    #myrow {
        max-height: 410px;
        overflow: auto;

    }

    #buttongroup {
        margin-top: 2%;
        margin-bottom: 0%;
        width: 100;
    }

    .span-style {
        color: #009688;
        font-weight: bold;
        font-size: 10pt
    }

    body {
        background: #FAFAFA !important;
    }

    .column {
        float: left;
        width: 100%;
        background-color: #ffffff
    }

    .project ::-webkit-scrollbar-track {
        -webkit-box-shadow: inset 0 0 6px #fff;
        -webkit-border-radius: 10px;
        border-radius: 10px;
    }

    .column2 {
        float: left;
        width: 49%;
        min-height: 50px;
        max-height: 50px;
        background-color: #ffffff
    }

    .no-margins {
        margin: 0px 0px 0px 0px;
    }

    /* Clear floats after the columns */

    .project .row:after {
        content: "";
        display: table;
        clear: both;
    }

    .inputText input[type=text] {
        width: 100%;
        height: 20px;
        box-sizing: border-box;
        border: 2px solid #ccc;
        border-radius: 4px;
        font-size: 13px;
        background-color: white;
        margin-top: 5px;
        background-position: 10px 10px;
        background-repeat: no-repeat;
        padding: 12px 20px 12px 10px;
        -webkit-transition: width 0.4s ease-in-out;
        transition: width 0.4s ease-in-out;
        max-width: 100%;
        opacity: 0.95;

    }

    .project .b-card {
        border-radius: 10px;
        margin: 1px 1px 1px 1px;
        opacity: 0.95;
        cursor: pointer;
    }

    .project input[type=text]:focus {
        width: 100%;
    }

    .project input[type="create"] {
        outline: none;
        color: #fff;
        padding: 0.6em 1em;
        font-size: 1em;
        border-radius: 10px;
        text-align: center;
        -webkit-appearance: none;
        background: #009688;
        border: 2px solid #009688;
        -webkit-transition: 0.5s all;
        -moz-transition: 0.5s all;
        transition: 0.5s all;
        width: 100%;
        cursor: pointer;
        margin-top: 5px
    }

    .project input[type="create"]:hover {
        background: transparent;
        color: #009688;
        letter-spacing: 3px;
    }

    .project textarea {
        box-sizing: border-box;
        border: 2px solid #ccc;
        border-radius: 4px;
    }

    #content {
        position: relative;
        opacity: 0.93;

    }

    #content #contentchild {
        position: absolute;
        top: 0px;
        width: 100%
    }

    #myselect {
        box-sizing: border-box;
        border: 2px solid #ccc;
        border-radius: 4px;
        margin-top: 1%;
    }

    .project .wrapper2 {
        width: 100%;
        height: 2px;
        left: 0;
        right: 0;
        bottom: 0;
        top: 0;
        margin-top: 2%;
        margin-bottom: 0%;
    }

    .project .loader2 {
        height: 100%;
        display: flex;
        -webkit-transform: translateZ(0);
        transform: translateZ(0);
    }

    .project .loader2 div {
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

    .project #multiselect {
        box-sizing: border-box;
        border: 2px solid #ccc;
        border-radius: 4px;
        margin-bottom: 2%;
        padding: 0px 0px 0px 0px;
    }

    .project #myul {
        box-sizing: border-box;
        border: 2px solid #ccc;
        border-radius: 4px;
        min-height: 30px;
        max-height: 88px;
        overflow: auto
    }

    .project #myli {
        padding-top: 2px;
        padding-bottom: 3px;
        padding-left: 8px
    }

    .project .multiselect,
    .multiselect__input,
    .multiselect__single {
        font-family: inherit;
        font-size: 10pt;
        -ms-touch-action: manipulation;
        touch-action: manipulation
    }
</style>
