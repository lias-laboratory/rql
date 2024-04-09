<template>
  <div class="rulesarea no-margin-top" v-if="display">
    <label> </label>
    <div class="row no-margin-top">
      <div class="col-lg-9">
        <div class="row" v-if="displayResult == false">
          <div class="col-lg-6">
            <div class="row">
              <div class="col-lg-4">
                <h3>Support:</h3>
              </div>
              <div class="col-lg-6">
                <input type="range" style="width: 100%" v-model="support" min="0" max="100" step="1">
              </div>
              <div class="col-lg-2">
                <input type="number" style="width: 50px" min="0" max="100" v-model="support">
              </div>
            </div>
          </div>
          <div class="col-lg-6">
            <div class="row">
              <div class="col-lg-4">
                <h3>Confidence:</h3>
              </div>
              <div class="col-lg-6">
                <input type="range" style="width: 100%" v-model="confiance" min="0" max="100" step="1">
              </div>
              <div class="col-lg-2">
                <input type="number" style="width: 50px" min="0" max="100" v-model="confiance">
              </div>
            </div>
          </div>
        </div>
        <div class="row" v-if="displayResult == false">
          <div class=" col-lg-4"></div>
          <b-btn variant="info" class="col-lg-4 btn btn-primary btn-lg form-control" id="btn-get-rules" type="button" @click="getAllRules">Generate rules</b-btn>
        </div>
        <div class="row">
          <div class="col-lg-11" v-if="displayResult == true">
            <h2 v-if="summ.length == 0">Rules Computation...</h2>
          </div>
        </div>
        <div class="table-header" v-if="summ.length != 0 && displayResult == true">
            <span class="table-title">Generated rules</span>
            <div class="actions">
              <a class="waves-effect btn-flat nopadding" href="javascript:undefined" @click="print">
                <i class="material-icons">print</i>
              </a>
              <a class="waves-effect btn-flat nopadding" :href="link" download>
                <i class="material-icons">description</i>
              </a>
              <a class="waves-effect btn-flat nopadding" href="javascript:undefined" @click="dosearch">
                <i class="material-icons">search</i>
              </a>
            </div>
          </div>
        <div class="loader" v-if="summ.length == 0 && displayResult == true && loading"></div>
        <div class="row" v-if="summ.length != 0 && displayResult == true && searching">
        <form class="col-lg-3">
          <input name="search1" type="text" v-model="searchL" @keyup="filterRows" placeholder="Left Attributes">
        </form>
        <form class="col-lg-3">
            <input name="search2" type="text" v-model="searchR" @keyup="filterRows" placeholder="Right Attribute">
        </form>
        </div>
        <div id="div0" v-if="summ.length != 0 && displayResult == true">
          <div>
            <datatable title="Generated rules" :columns="columns" :rows="filteredRules"></datatable>
          </div>
        </div>
      </div>
      <div class="col-lg-3" v-if="displayResult == true && summ.length != 0">
        <div class="row">
          <div class="col-lg-1"></div>
          <button class="col-lg-9 btn btn-primary form-control btn-danger" id="actualize" type="button" @click="actualise">Change parameters</button>
        </div>
        <div id="summary">
          <b-card no-body="">
            <h4 slot="header">Execution summary</h4>
            <b-card-body>
              <p class="card-text" style="font-weight: bold;">
                Action: generate rules
              </p>
            </b-card-body>
            <b-list-group flush="">
              <b-list-group-item>Execution time:
                <span>{{summ.timeExecution/1000}}</span> second </b-list-group-item>
              <b-list-group-item>Query attributes:
                <span>{{summ.attributesList.length}}
                  <b-btn v-b-toggle.collapse01 variant="info" size="sm">
                    <i class="fa fa-angle-down"></i>
                  </b-btn>
                </span>
                <div>
                  <b-collapse class="mt-2" id="collapse01">
                    <ul class="list-group">
                      <li class="list-group-item my-li" v-b-tooltip v-for="option in summ.attributesList">{{option}}</li>
                    </ul>
                  </b-collapse>
                </div>
              </b-list-group-item>
              <b-list-group-item>Total number of generated rules:
                <span>{{rows.length}}</span>
              </b-list-group-item>
            </b-list-group>
            <b-card-footer>RQL</b-card-footer>
          </b-card>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import Utils from './UtilsMixins'
  import DataTable from './vue-materialize-datatable'
  export default {
    mixins: [Utils],
    components: {
      'datatable': DataTable
    },
    name: 'rulesarea',
    data () {
      return {
        results: [],
        display: '',
        style: { background: '#FFF' },
        leftside: '',
        rightside: '',
        printRules: false,
        ExactRule: 'true',
        searching: false,
        summ: '',
        loading: true,
        searchL: '',
        searchR: '',
        queryID: '',
        excel: '',
        filteredRules: [],
        link: '',
        support: 0,
        confiance: 100,
        displayLoader: false,
        displayResult: false,
        columns: [
          { label: 'Left attributes', field: 'leftAttributes' },
          { label: 'Right attribute', field: 'rightAttribute' },
          { label: 'Support %', field: 'support' },
          { label: 'Confidence %', field: 'confidence' },
          { label: 'Lift', field: 'lift' }
        ]
      }
    },
    props: ['pquery', 'isdisplay'],
    computed: {
    },
    watch: {
      isdisplay (isdisplay) {
        this.display = isdisplay
      },
      pquery (queryID) {
        this.queryID = queryID
        this.results = []
        this.summ = ''
        this.displayResult = false
      }
    },
    methods: {
      filterRows () {
        // eslint-disable-next-line
        self = this
        this.filteredRules = this.rows.filter(function (row) { return (row.leftAttributes.replace(/\s/g, '').toUpperCase().includes(self.searchL.replace(/\s/g, '').toUpperCase()) && row.rightAttribute.replace(/\s/g, '').toUpperCase().includes(self.searchR.replace(/\s/g, '').toUpperCase())) })
      },
      getAllRules () {
        this.displayResult = true
        this.results = []
        this.rows = []
        this.summ = ''
        this.ExactRule = 'true'
        this.link = 'server/excel/' + this.queryID + '/rule?' + 'support=' + this.support / 100 + '&confidence=' + this.confiance / 100
        var token = localStorage.getItem('TOKEN')
        this.$http.get('query/{id}/rule', { params: { id: this.queryID, support: this.support / 100, confidence: this.confiance / 100 } }, {headers: {'Authorization': token}}).then((response) => {
          this.results = response.data.exactRule
          this.rows = response.data.exactRule
          this.filteredRules = this.rows
          this.summ = response.data.summary
        }, (response) => {
          this.loading = false
        })
      },
      actualise () {
        this.displayResult = false
        this.results = []
      },
      dosearch () {
        this.searching = !this.searching
      },
      print () {
        let win = window.open('')
        win.document.write(this.renderTable())
        win.print()
        win.close()
      },
      renderTable () {
        var table = '<table border="1"><thead>'
        table += '<tr>'
        for (var i = 0; i < this.columns.length; i++) {
          const column = this.columns[i]
          table += '<th>'
          table += column.label
          table += '</th>'
        }
        table += '</tr>'
        table += '</thead><tbody>'
        for (var l = 0; l < this.rows.length; l++) {
          const row = this.rows[l]
          table += '<tr>'
          for (var j = 0; j < this.columns.length; j++) {
            const column = this.columns[j]
            table += '<td>'
            table += this.collect(row, column.field)
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
      collect (obj, field) {
        if (typeof (field) === 'function') {
          return field(obj)
        } else if (typeof (field) === 'string') {
          return this.dig(obj, field)
        } else {
          return undefined
        }
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped="">
  .table-title {
		font-size: 15pt;
		color: #009688;
	}
  h1,
  h2 {
    font-weight: normal;
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
    max-height: 500px;
    overflow: auto;
  }

  .my-input {
    max-height: 30px;
    margin-bottom: 10px;
  }

  #fleche {
    margin-left: 0%;
  }

  .category:focus option:first-of-type {
    display: none;
  }

  .zoom div img {
    -webkit-transform: scale(1);
    transform: scale(1);
    -webkit-transition: .3s ease-in-out;
    transition: .3s ease-in-out;
  }

  .zoom {
    margin-left: 5%;
    display: inline-block;
  }

  .zoom div {
    display: inline-block;
  }

  .zoom div:hover img {
    -webkit-transform: scale(1.3);
    transform: scale(1.3);
  }

  .col-lg-11 h2,
  h3 {
    margin-left: 10px;
    margin-bottom: 0%;
    display: inline-block
  }

  .inline-header {
    font-size: 12pt;
    font-weight: bold;
  }

  span {
    font-weight: bold
  }

  #icon-txt {
    margin-bottom: 0%;
    margin-left: 20%;
  }

  #icon-pdf {
    margin-bottom: 0%;
    margin-left: 10%;
  }

  #summary {
    margin-top: 2%;
    margin-right: 2%;
    max-height: 400px;
    overflow: auto;
  }

  b-card {
    width: 100%;
  }

  input[type=number]::-webkit-inner-spin-button {
    -webkit-appearance: none;
  }

  #leftAtt {
    font-size: 9pt;
  }

  #rightAtt {
    font-size: 9pt;
  }

  .rulesarea {
    font-family: "Helvetica", Helvetica, Arial, sans-serif;
  }

  .list-inline-item {
    margin-left: 0%
  }

  #actualize {
    margin-left: 0%
  }

  #btn-get-rules {
    margin-top: 10%;
    height: 40pt;
  }

  .cardbody {
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px;
  }

  .my-li {
    font-size: 8pt;
    font-weight: bolder;
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px;
  }

  input[type=text] {
    width: 100%;
    height: 30px;
    box-sizing: border-box;
    border: 2px solid #ccc;
    border-radius: 4px;
    font-size: 13px;
    background-color: white;
    background-position: 10px 10px;
    background-repeat: no-repeat;
    padding: 12px 20px 12px 10px;
    -webkit-transition: width 0.4s ease-in-out;
    transition: width 0.4s ease-in-out;
    max-width: 100%;
    margin-top: 1%;
    margin-bottom: 1% 
  }
  input[type=text]:focus {
    width: 100%;
  }
  .table-header .actions {
		display: -webkit-flex;
		margin-left: auto;
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
  .actions a{
    color:#1a1818
  }
</style>
