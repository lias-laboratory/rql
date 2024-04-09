<template>
  <div class="sqlarea" v-if="display">
    <label> </label>
    <div class="row" id="div00">
      <b-alert v-if="!isSelect" id="operationSuccess" show dismissible>
          <strong>Success!</strong> Operation success
        </b-alert>
      <div class="col-lg-12" v-if="isSelect == true">
          <div class="col-lg-9" v-if="isSelect == true">
              <div class="table-header" v-if="results.length != 0">
                  <span class="table-title">Computed Results:</span>
                  <div class="actions">
                    <a class="waves-effect btn-flat nopadding" href="javascript:undefined" @click="print">
                      <i class="material-icons">print</i>
                    </a>
                    <a :href="link" download v-if="results.length != 0">
                      <i class="material-icons" title="Download the ResultSet as Xlsx file">description</i>
                    </a>
                  </div>
                </div>
          </div>
      </div>
      <div class="col-lg-9" v-if="isSelect == true">
        <h2 v-if="results.length == 0">SQL Request Results Computation...</h2>
        <div>
        </div>
        <div class="loader" v-if="loading &&  results.length == 0"></div>
        <div class="alert alert-danger sqlError" v-if="!loading">
          <strong>SQL Statement error !</strong><br>
          <label>Status: {{ErrorMessage.status}}</label><br>
          <label>Status Text: {{ErrorMessage.statusText}}</label><br>
          <label>Error: {{ErrorMessage.bodyText}}</label>
        </div>
        <div id="div0" v-if="results.length != 0">
          <b-table style="background-color:#fff" :bordered="true" show-empty stacked="md" :items="rows" :fields="columnList" :current-page="currentPage" :per-page="perPage" :filter="filter" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" @filtered="onFiltered">
          </b-table>
        </div>
        <div class="row" v-if="results.length != 0" style="margin-top:1%">
          <div class="col-lg-4"></div>
          <div class="col-lg-5">
            <b-input-group>
              <b-pagination :total-rows="totalRows" :per-page="perPage" v-model="currentPage">
            </b-pagination></b-input-group>
          </div>
          <div class="col-lg-4"></div>
        </div>
      </div>
      <div class="col-lg-3" style="padding-left:2%">
        <div v-if="results.length != 0 && isSelect">
          <div class="row">
            <b-input-group class="row">
              <b-form-input class="col-lg-11" v-model="filter" placeholder="Type to Search">
            </b-form-input></b-input-group>
          </div>
          <div class="row">
            <b-input-group class="row">
              <b-form-select class="col-lg-8" v-model="sortBy" :options="sortOptions">
                <option slot="first" :value="null">--Sort By--</option>
              </b-form-select>
              <b-form-select class="col-lg-3" :disabled="!sortBy" v-model="sortDesc">
                <option :value="false">Asc</option>
                <option :value="true">Desc</option>
              </b-form-select>
            </b-input-group>
          </div>
          <div class="row">
            <b-input-group class="row">
              <b-form-select class="col-lg-11" :options="pageOptions" v-model="perPage">
            </b-form-select></b-input-group>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Utils from './UtilsMixins'
  import DataTable from 'vue-materialize-datatable'
  class Column {
    constructor (key, label, sortable) {
      this.label = label
      this.key = key
      this.sortable = sortable
    }
  }
  export default {
    mixins: [Utils],
    components: {
      'datatable': DataTable
    },
    name: 'sqlArea',
    data () {
      return {
        results: [],
        display: '',
        select: false,
        message: false,
        isSelect: true,
        loading: true,
        ErrorMessage: '',
        link: '',
        columnList: [],
        colum: [],
        currentPage: 1,
        perPage: 5,
        totalRows: 0,
        pageOptions: [5, 10],
        sortBy: null,
        sortDesc: false,
        filter: null,
        rows: []

      }
    },
    props: ['queryID', 'isdisplay'],
    computed: {
      sortOptions () {
        // Create an options list from our fields
        return this.columnList
          .filter(f => f.sortable)
          .map(f => { return { text: f.label, value: f.key } })
      }
    },
    methods: {
      info (item, index, button) {
        this.modalInfo.title = `Row index: ${index}`
        this.modalInfo.content = JSON.stringify(item, null, 2)
        this.$root.$emit('bv::show::modal', 'modalInfo', button)
      },
      resetModal () {
        this.modalInfo.title = ''
        this.modalInfo.content = ''
      },
      onFiltered (filteredItems) {
        // Trigger pagination to update the number of buttons/pages due to filtering
        this.totalRows = filteredItems.length
        this.currentPage = 1
      },
      print () {
        let win = window.open('')
        console.log(this.renderTable())
        win.document.write(this.renderTable())
        win.print()
        win.close()
      },
      printData () {
        var divToPrint = document.getElementById('div0')
        let newWin = window.open('')
        newWin.document.write(divToPrint.outerHTML)
        newWin.print()
        newWin.close()
      },

      renderTable () {
        var table = '<table border="1" cellpadding="3" style="page-break-inside:always;page-break-after:always"><thead>'
        table += '<tr style="page-break-inside:always; page-break-after:always">'
        for (var i = 0; i < this.columnList.length; i++) {
          const column = this.columnList[i]
          table += '<th>'
          table += column.label
          table += '</th>'
        }
        table += '</tr>'
        table += '</thead><tbody>'
        for (var f = 0; f < this.rows.length; f++) {
          const row = this.rows[f]
          table += '<tr>'
          for (var j = 0; j < this.columnList.length; j++) {
            const column = this.columnList[j]
            table += '<td>'
            table += this.collect(row, column.key)
            table += '</td>'
          }
          table += '</tr>'
        }
        table += '</tbody></table>'
        return table
      },
      dig (obj, selector) {
        var result = obj
        const splitter = selector.split('.')

        for (let i = 0; i < splitter.length; i++) {
          if (result === undefined) {
            return undefined
          }
          result = result[splitter[i]]
        }

        return result
      },
      collect (obj, key) {
        if (typeof (key) === 'function') {
          return key(obj)
        } else if (typeof (key) === 'string') {
          return this.dig(obj, key)
        } else {
          return undefined
        }
      }
    },
    watch: {
      isdisplay (isdisplay) {
        this.display = isdisplay
      },
      queryID (queryID) {
        this.loading = true
        this.results = []
        this.columnList = []
        this.select = true
        this.isSelect = true
        var projectID = localStorage.getItem('PROJECTID')
        this.link = 'server/excel/' + queryID + '/resultset?projectID=' + projectID
        var token = localStorage.getItem('TOKEN')
        this.$http.get('query/{id}/resultset', { params: { id: this.queryID, projectID: projectID } }, {headers: {'Authorization': token}}).then((response) => {
          this.results = response.data
          this.rows = this.results.rows
          this.totalRows = this.results.rows.length
          this.isSelect = response.data.isSelect
          for (var element of this.results.header) {
            this.columnList.push(new Column(element, element, true))
          }
          this.$emit('refreshData', Math.random())
        }, (response) => {
          this.loading = false
          this.ErrorMessage = response
        })
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped="">
  .table-header .actions {
    display: -webkit-flex;
    margin-left: auto;
  }

  .table-title {
    font-size: 15pt;
    color: #009688;
  }

  .col-lg-3 .row {
    margin-bottom: 2%
  }

  .table-header {
    padding-right: 14px;
    -webkit-align-items: center;
    -ms-flex-align: center;
    align-items: center;
    display: flex;
    -webkit-display: flex;
    border-bottom: solid 1px #DDDDDD;
  }
 
  .actions a {
    color: #1a1818
  }
  h1,
  h2 {
    font-weight: normal;
  }

  #div0 {
    max-height: 500px;
    overflow: auto;
  }

  #operationSuccess {
    margin:auto
  }

  td {
    max-width: 210px;
    min-width: 210px;
    font-size: 9pt
  }

  th {
    max-width: 210px;
    min-width: 210px;
    font-size: 9pt;
    overflow: hidden;
    text-overflow: ellipsis;
  }
</style>