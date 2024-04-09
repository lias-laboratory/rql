<template>
  <div class="databaseschema">
    <div class="row">
      <div class="col-lg-12">
        <h2>Database schema</h2>
        <div class="row">
          <div class="col-lg-12" style="margin-right: 0px;">
            <form>
              <input type="text" name="search" v-model="search" placeholder="Search..">
            </form>
          </div>
        </div>
        <div role="tablist" class="tablist">
          <div v-for="(table, index) in filteredTables">
            <b-card no-body class="mb-1">
              <b-card-header header-tag="header" class="p-1" role="tab">
                <b-btn block href="#" v-b-toggle="'accordion' + index" size="sm" variant="info">{{table.tableName}}</b-btn>
              </b-card-header>
              <b-collapse :id="'accordion' + index" accordion="my-accordion" role="tabpanel">
                <b-card-body class="card-body">
                  <ul class="list-group">
                    <li v-b-tooltip :title="valeur.columnType" class="list-group-item my-li" v-for="valeur in table.columnsName">{{valeur.columnName}}</li>
                  </ul>
                </b-card-body>
              </b-collapse>
            </b-card>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Utils from './UtilsMixins'
  export default {
    name: 'databaseschema',
    mixins: [Utils],
    data () {
      return {
        results: [],
        displayResults: [],
        search: ''
      }
    },
    computed: {
      filteredTables () {
        var self = this
        return this.results.filter(function (table) { return table.tableName.toLowerCase().indexOf(self.search.toLowerCase()) >= 0 })
      }
    },
    methods: {
      refreshData () {
        var token = localStorage.getItem('TOKEN')
        var projectID = localStorage.getItem('PROJECTID')
        this.$http.get('table', {params: {projectID: projectID}}, {headers: {'Authorization': token}}).then((response) => {
          this.results = response.data.tables
          this.displayResults = this.results
        }, (response) => {
        })
      }
    },
    mounted () {
      this.refreshData()
    }

  }
</script>

<style scoped>
  #table {
    max-width: 100%;
  }

  #category:focus option:first-of-type {
    display: none;
  }

  #category {
    width: 100%;
  }

  a i {
    cursor: pointer;
  }

  .container {
    width: auto;
    height: auto;
  }

  input[type=text] {
    width: 80px;
    height: 30px;
    box-sizing: border-box;
    border: 2px solid #ccc;
    border-radius: 4px;
    font-size: 13px;
    background-color: white;
    margin-bottom: 5%;
    background-position: 10px 10px;
    background-repeat: no-repeat;
    padding: 12px 20px 12px 10px;
    -webkit-transition: width 0.4s ease-in-out;
    transition: width 0.4s ease-in-out;
    max-width: 100%
  }

  input[type=text]:focus {
    width: 100%;
  }

  .card-body {
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px;
  }

  .my-li {
    font-size: 8pt;
    font-weight: bolder;
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px;
  }

  .tablist {
    width: 100%;
    max-height: 400px;
    overflow: auto
  }

  h1,
  h2 {
    font-weight: normal;
    margin-bottom: 5px;
    padding-left: 0px;
    font-size: 13pt
  }
</style>
