<template>
  <div class="queryarea">
    <div>
      <div class="row" id="div00">
        <div class="col-lg-12">
          <h2 id="my-header">Submit your RQL or SQL query:
            <b-btn @click="setDirection" style="display:inline-block" v-b-toggle.collapse1 variant="info" size="sm">
              <i class="fa fa-angle-down" v-if="down" style="width:15px"></i>
              <i class="fa fa-angle-up" v-if="!down" style="width:15px"></i>Assisted mode</b-btn>
          </h2>
          <div>
            <b-collapse class="mt-2" id="collapse1">
              <b-card>
                <h3>Build your query step by step:</h3>
                <div class="row">
                  <div class="col-lg-6">
                    <div class="row">
                      <div class="col-lg-12">
                        <b-card no-body="">
                          <h3 slot="header">Step 1: type of dependency</h3>
                          <b-card-body class="father">
                            <div class="row">
                              <div class="col-lg-10">
                                  <!--b-form-select v-model="QueryType" :options="options" class="mb-3" placeholder="Select your query type" /-->
                                <multiselect :multiple="true" :max="1" v-model="QueryType" :options="options" placeholder="Select your query type"></multiselect>
                              </div>
                            </div>
                          </b-card-body>
                        </b-card>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-lg-12">
                        <b-card no-body="">
                          <h3 slot="header">Step 2: scope</h3>
                          <b-card-body class="father">
                              <div class="row">
                                  <div class="col-lg-4">
                                    <label class="radio-inline">
                                      <input name="optradio1" type="radio" v-model="istable" value="table">Table</label>
                                  </div>
                                  <div class="col-lg-4">
                                      <label class="radio-inline">
                                        <input name="optradio1" type="radio" v-model="istable" value="view">View</label>
                                    </div>
                                </div>
                            <div class="row" v-if="istable == 'table'">
                              <div class="col-lg-12">
                                <label for="datatbl">Table:</label>
                                <multiselect id="datatbl" :multiple="true" :max="1" v-model="datatable" :options="options2" placeholder="Select your data table"></multiselect>
                              </div>
                            </div>
                            <div class="row" v-if="istable == 'table'">
                                <div class="col-lg-4">
                                    <label class="radio-inline">
                                      <input name="optradio2" type="radio" v-model="isAllData" value="All">All data</label>
                                  </div>
                                  <div class="col-lg-4">
                                      <label class="radio-inline">
                                        <input name="optradio2" type="radio" v-model="isAllData" value="Subset">Subset</label>
                                    </div>
                              </div>
                            <div class="row" v-if="istable == 'view'">
                                <div class="col-lg-12">
                                  <multiselect id="datatbl" :multiple="true" :max="1" v-model="selectedSQL" :custom-label="customLabel" track-by="name" :options="sqlQueries" placeholder="Select your SQL query"></multiselect>
                                </div>
                              </div>
                            <div class="row" v-if="istable == 'table' && isAllData == 'Subset'">
                              <div class="col-lg-12">
                                <label for="wherecdt">Where condition:</label>
                                <input class="form-control" id="wherecdt" type="text" @blur="contructQuery" v-model="whereCondition" placeholder="Example: value > 2 AND name like '%rql%'">
                              </div>
                            </div>
                          </b-card-body>
                        </b-card>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="row">
                      <div class="col-lg-12">
                        <b-card no-body="" v-if="istable == 'table'">
                          <h3 slot="header">Step 3: attributes</h3>
                          <b-card-body class="father">
                            <div class="row">
                              <div class="col-lg-10">
                                <multiselect :closeOnSelect="false" :multiple="true" v-model="AttributeList" :options="options3" placeholder="Select the attributes"></multiselect>
                              </div>
                              <button class="btn-light" type="button" @click="selectAll">All</button>
                            </div>
                          </b-card-body>
                        </b-card>
                        <b-card no-body="" v-if="istable == 'view'">
                          <h3 slot="header">Step 3: attributes</h3>
                          <b-card-body class="father">
                            <div class="row">
                              <div class="col-lg-10">
                                <input type="text" placeholder="List attributes" v-model="attributSet" @blur="contructQuery2">
                  
                              </div>
                            </div>
                          </b-card-body>
                        </b-card>
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-lg-12">
                        <b-card no-body="">
                          <h3 slot="header">Step 4: bindings</h3>
                          <b-card-body class="father">
                            <div class="row">
                              <div class="col-lg-4">
                                <label class="radio-inline">
                                  <input name="optradio3" type="radio" v-model="isConditional" value="false">Unbound tuple variables</label>
                              </div>
                              <div class="col-lg-3">
                                <label class="radio-inline">
                                  <input name="optradio3" type="radio" v-model="isConditional" value="true">Bound tuple variables</label>
                              </div>
                            </div>
                            <div class="row" v-if="isConditional == 'true' ">
                              <div class="col-lg-12">
                                <label for="wherecdt">Binding condition:</label>
                                <input class="form-control" id="wherecdt" type="text" @blur="contructQuery" v-model="whereConditionRql" placeholder="Example: t1.value < t2.value">
                              </div>
                            </div>
                          </b-card-body>
                        </b-card>
                      </div>
                    </div>
                    <div class="row" v-if="QueryType == 'Metric Functional Dependencies'">
                      <div class="col-lg-12">
                        <b-card no-body="">
                          <h3 slot="header">Step 5: dependency parameters</h3>
                          <b-card-body>
                            <div class="row">
                              <div class="col-lg-4">
                                <label>Threshold:</label>
                              </div>
                              <div class="col-lg-6">
                                <input type="range" style="width: 100%" v-model="threshold" min="0" max="1" step="0.01">
                              </div>
                              <div class="col-lg-2">
                                <input type="number" style="width: 50px" min="0" max="1" v-model="threshold">
                              </div>
                            </div>
                          </b-card-body>
                        </b-card>
                      </div>
                    </div>
                  </div>
                </div>
              </b-card>
            </b-collapse>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-lg-9" id="div0">
          <div>
            <textarea class="form-control" id="query-field" rows="7" placeholder="Type you RQL or SQL query" v-model="query"></textarea>
          </div>
          <div class="row">
            <button class="btn btn1 btn-primary btn-block col-lg-3" id="area-clean-btn" type="button" v-on:click="AreaClean">Reset query area</button>
            <button class="btn btn1 btn-primary btn-block col-lg-4" id="submit-btn" type="button" v-on:click="querySend">Submit query</button>
            <button class="btn btn1 btn-primary btn-block col-lg-3" type="button" v-on:click="newFavorite">
              <i class="fa fa-star"></i> Bookmark query
            </button>
          </div>
          <div class="row " v-if="isAddFavorite">
            <div class="col-lg-12">
              <b-card class="addfavorite" title="Add query to favorites">
                      <input type="text" placeholder="name" v-model="favoriteName">
                      <input type="text" placeholder="description" v-model="favoriteDescription">
                      <div class="row">
                          <div class="col-lg-8">
                          </div>
                          <div class="col-lg-2">
                              <b-btn style="background-color:#E53935" @click="cancelAddFavorite" class="form-control">Cancel</b-btn>
                          </div>
                          <div class="col-lg-2">
                              <b-btn variant="info" @click="createFavorite" class="form-control">Add</b-btn>
                          </div>
                      </div>
          <b-alert style="margin-bottom:0%; margin-top:2%" :show="showError" variant="danger">{{errorMessage}}</b-alert>
              </b-card>
            </div>
        </div>
        </div>
        <div class="col-lg-3">
          <div class="row">
            <div class="row">
              <div class="col-lg-3">Bookmarks</div>
              <div class="col-lg-9">
                  <input style="width:80%; margin:0px 0px 5px 0px" type="text" name="search" v-model="search" placeholder="Search..">
              </div>
            </div>
            <div class="col-lg-12">
              <div role="tablist" class="tablist">
                <div v-for="(fav, index) in filteredList">
                        <b-card no-body class="mb-1">
                          <b-card-header header-tag="header" class="p-1" role="tab">
                            <div class="row">
                            <div class="col-lg-1">
                            <b-btn class="no-margins" @click="elementClicked(fav)" variant="info" size="sm"><i class="fa fa-chevron-left" style="margin:auto"></i></b-btn>
                          </div>
                          <div class="col-lg-1">
                              <b-btn class="no-margins" @click="deleteFavorite(fav)" style="background-color:#E53935" size="sm"><i class="fa fa-trash" style="margin:auto"></i></b-btn>
                            </div>
                            <div class="col-lg-10">
                            <b-btn block href="#" class="fav-btn" v-b-toggle="'accordion' + 2 +index" size="sm" variant="info">{{fav.name}} - {{fav.type}}</b-btn>
                          </div>
                          </div>
                          </b-card-header>
                          <b-collapse :id="'accordion' + 2 + index" accordion="my-accordion" role="tabpanel">
                            <b-card-body class="card-corps" id="card-body">
                                <p style="font-size: 9pt; margin-bottom: 0%">
                                    <span style="color:#0097A7">Creation date: </span>{{fav.creationDate}}</p>
                              <p style="font-size: 9pt">
                                <span style="color:#0097A7">Description: </span>{{fav.description}}</p>
                            </b-card-body>
                          </b-collapse>
                        </b-card>
                </div>
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
    components: {
      Multiselect
    },
    name: 'queryarea',
    data () {
      return {
        query: '',
        QueryType: [],
        isAllData: 'All',
        istable: 'table',
        query1: '',
        queryID: '',
        queryID2: '',
        threshold: 0.1,
        isConditional: 'false',
        datatable: [],
        whereCondition: '',
        whereConditionRql: '',
        AttributeList: [],
        selectedSQL: [],
        tableChoice: '',
        options: ['Functional Dependencies', 'Sequential Dependencies', 'Metric Functional Dependencies', 'Association Rules', 'Null Values Dependencies'],
        options2: [],
        options3: [],
        options4: [],
        display: 0,
        display1: false,
        display2: false,
        showModal: false,
        displayResults: false,
        attributSet: '',
        rqlParsed: '',
        load: false,
        down: true,
        search: '',
        favoriteList: [],
        isAddFavorite: false,
        favoriteDescription: '',
        favoriteName: '',
        sqlQueries: [],
        favorite: {
          id: '',
          name: '',
          creationDate: '',
          description: '',
          type: 'RQL',
          query: '',
          projectID: ''
        },
        favOptions: ['RQL', 'SQL'],
        showError: false,
        errorMessage: ''
      }
    },
    props: ['constructedquery'],
    watch: {
      istable (value) {
        var listAtt = ''
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            sqlQuery: this.selectedSQL[0].query,
            subsetWhere: this.whereCondition,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      constructedquery (constructedquery) {
        console.log(constructedquery)
        this.query = constructedquery
      },
      selectedSQL () {
        var listAtt = ''
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            sqlQuery: this.selectedSQL[0].query,
            subsetWhere: this.whereCondition,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      datatable (value) {
        var listAtt = ''
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var sqlQuery = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        this.tableChoice = value[0]
        this.options3 = []
        this.AttributeList = []
        var token = localStorage.getItem('TOKEN')
        this.getAttributesList()
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: value.join(''),
            subsetWhere: this.whereCondition,
            sqlQuery: sqlQuery,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      QueryType () {
        var listAtt = ''
        this.AttributeList = []
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        if (this.QueryType.length > 0) {
          this.getAttributesList()
          var sqlQuery = ''
          if (this.selectedSQL.length > 0) {
            sqlQuery = this.selectedSQL[0].query
          }
          var token = localStorage.getItem('TOKEN')
          this.$http.get('queryconstruction', {
            params: {
              QueryType: this.QueryType[0],
              isTable: this.istable,
              isALLData: this.isAllData,
              tableName: this.tableChoice,
              subsetWhere: this.whereCondition,
              sqlQuery: sqlQuery,
              attributesList: listAtt,
              isConditional: this.isConditional,
              conditionalWhere: this.whereConditionRql,
              tolerence: this.threshold
            }
          }, { headers: { 'Authorization': token } }).then((response) => {
            this.query = response.data
          }, (response) => {
          })
        }
      },
      isAllData () {
        var sqlQuery = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        var listAtt = ''
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            subsetWhere: this.whereCondition,
            sqlQuery: sqlQuery,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      isConditional () {
        var sqlQuery = ''
        var listAtt = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            subsetWhere: this.whereCondition,
            sqlQuery: sqlQuery,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      AttributeList () {
        var sqlQuery = ''
        var listAtt = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            subsetWhere: this.whereCondition,
            sqlQuery: sqlQuery,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      threshold () {
        var sqlQuery = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        var listAtt = ''
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            subsetWhere: this.whereCondition,
            sqlQuery: sqlQuery,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      }
    },
    computed: {
      filteredList () {
        var self = this
        return this.favoriteList.filter(function (fav) { return (fav.name.toLowerCase().indexOf(self.search.toLowerCase()) >= 0 || fav.type.toLowerCase().indexOf(self.search.toLowerCase()) >= 0) })
      }
    },
    methods: {
      getAttributesList () {
        var token = localStorage.getItem('TOKEN')
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('table/{id}', {
          params: {
            id: this.datatable[0],
            QueryType: this.QueryType[0],
            projectID: projectID
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.options3 = response.data
        }, (response) => {
        })
      },
      customLabel (option) {
        return `${option.name} - ${option.creationDate}`
      },
      newFavorite () {
        this.favoriteDescription = ''
        this.favoriteName = ''
        this.isAddFavorite = true
      },
      cancelAddFavorite () {
        this.isAddFavorite = false
        this.favoriteDescription = ''
      },
      createFavorite () {
        this.showError = false
        var projectID = localStorage.getItem('PROJECTID')
        this.favorite.query = this.query
        this.favorite.name = this.favoriteName
        this.favorite.description = this.favoriteDescription
        this.favorite.projectID = projectID
        this.$http.post('project/favorite/create', JSON.stringify(this.favorite)).then((response) => {
          this.getFavoriteQueries()
          this.getFavoriteSqlQueries()
          this.isAddFavorite = false
        }, (response) => {
          this.showError = true
          this.errorMessage = response.bodyText
        })
      },
      elementClicked (fav) {
        this.query = fav.query
      },
      deleteFavorite (fav) {
        this.$http.delete('project/favorite/{id}', { params: { id: fav.id } }).then((response) => {
          this.getFavoriteQueries()
        }, (response) => {
        })
      },
      getTableHeader () {
        var token = localStorage.getItem('TOKEN')
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('table/header', { params: { projectID: projectID } }, { headers: { 'Authorization': token } }).then((response) => {
          this.options2 = response.data
        }, (response) => {
        })
      },
      getFavoriteQueries () {
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('project/{id}/favorite', { params: { id: projectID } }).then((response) => {
          this.favoriteList = response.data
        }, (response) => {
        })
      },
      getFavoriteSqlQueries () {
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('project/{id}/favorite/sql', { params: { id: projectID } }).then((response) => {
          this.sqlQueries = response.data
        }, (response) => {
        })
      },
      setDirection () {
        this.down = !this.down
      },
      querySend () {
        this.$emit('querySend', this.query)
      },
      AreaClean () {
        this.query = ''
      },
      contructQuery () {
        var sqlQuery = ''
        var listAtt = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        if (!this.istable.localeCompare('table')) {
          listAtt = this.AttributeList.join(', ')
        } else {
          listAtt = this.attributSet
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            sqlQuery: sqlQuery,
            subsetWhere: this.whereCondition,
            attributesList: listAtt,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      contructQuery2 () {
        var sqlQuery = ''
        if (this.selectedSQL.length > 0) {
          sqlQuery = this.selectedSQL[0].query
        }
        var token = localStorage.getItem('TOKEN')
        this.$http.get('queryconstruction', {
          params: {
            QueryType: this.QueryType[0],
            isTable: this.istable,
            isALLData: this.isAllData,
            tableName: this.tableChoice,
            sqlQuery: sqlQuery,
            subsetWhere: this.whereCondition,
            attributesList: this.attributSet,
            isConditional: this.isConditional,
            conditionalWhere: this.whereConditionRql,
            tolerence: this.threshold
          }
        }, { headers: { 'Authorization': token } }).then((response) => {
          this.query = response.data
        }, (response) => {
        })
      },
      selectAll () {
        this.AttributeList = this.options3.slice()
      },
      constructedquerysend () {
        this.$emit('constructedquerysend', this.query)
      },
      goBack () {
        this.displayResults = false
        this.display2 = false
        this.display1 = false
      },
      sendQuery () {
        this.load = true
        var token = localStorage.getItem('TOKEN')
        this.$http.post('query/typequery', this.query, { headers: { 'Authorization': token } }).then((response) => {
          this.isRqlQuery = response.data.type
          this.display2 = false
          this.display1 = false
          if (this.isRqlQuery === 'RQL') {
            this.queryID = response.data.queryKey
            this.options4 = response.data.attributesList
            this.rqlParsed = response.data.ruleParsed
            this.query3 = this.query
            this.showModal = true
            this.load = false
          } else {
            this.load = false
          }
        }, (response) => {
          this.load = false
        })
      }
    },
    mounted () {
      this.getTableHeader()
      this.getFavoriteQueries()
      this.getFavoriteSqlQueries()
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped="">
  .multiselect,
  .multiselect__input,
  .multiselect__single {
    font-family: inherit;
    font-size: 12px;
    -ms-touch-action: manipulation;
    touch-action: manipulation
  }

  h3 {
    font-weight: normal;
    font-size: 11pt
  }

  .row {
    margin-top: 1%
  }
  #my-bform{
    box-sizing: border-box;
        border: 2px solid #ccc;
        border-radius: 4px;
        font-size: 13px;
        margin-top:0.7%;
  }
  #my-header {
    margin-right: 10px;
    margin-top: 1%;
    margin-bottom: 0%
  }
  .addfavorite{
    width: 100%
  }
  #query-field {
    margin-bottom: 10px;
  }

  h1,
  h2 {
    font-weight: normal;
    font-size: 13pt
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  #div0 {
    width: 100%;
  }

  #div00 {
    width: 100%;
    margin-top: 0px;
  }

  .btn1 {
    width: 100%;
    margin: auto;
  }

  .btn {
    background-color: #0097A7;
    border: #0097A7;
  }

  .fd {
    font-size: 10pt;
  }

  .font8 {
    font-size: 9pt;
    width: 110%
  }

  .card-corps {
    max-height: 50px;
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px;
    overflow: auto;

  }

  #card-body {
    position: relative
  }

  #card-body #contentchild {
    position: absolute;
    bottom: 0px;
    right: 0px;
  }

  .queryarea {
    font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  }

  .tablist {
    max-height: 170px;
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px;
    overflow-y: auto;
    overflow-x: hidden;

  }

  #submit-btn {
    background-color: #13130d;
    border: #2c2c29;
  }

  #drop {
    border: 2px dashed #bbb;
    -moz-border-radius: 5px;
    -webkit-border-radius: 5px;
    border-radius: 5px;
    padding: 25px;
    text-align: center;
    font: 20pt bold, "Vollkorn";
    color: #bbb;
  }

  .no-margins {
    margin: 0px 0px 0px 0px;
  }
  .queryarea [type=text] {
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
    .father{
      position: relative
    }
    .father .daughter{
      position: absolute;
      top:5%;
      right: 3%

    }
    .fav-btn {
      text-align: left;
    }
</style>
