<template>
  <div class="checkrule" v-if="display">
    <div class="row">
      <div class="col-lg-9">
        <div v-if="!displayResult">
          <h2>Check a single rule:</h2>
          <div class="row no-margin-top">
          <div class="col-lg-5">
          <multiselect v-model="selectedleft" :multiple="true" :options="options" placeholder="Left attributes">
          </multiselect>
        </div>
          <i class="fa fa-arrow-right col-lg-1" id="fleche" aria-hidden="true"></i>
          <div class="col-lg-5">
          <multiselect v-model="selectedright" :multiple="true" :options="options" placeholder="Right attributes">
          </multiselect>
        </div>
        </div>
      </div>
        <div class="row" id="params" v-if="!displayResult">
            <div class="col-lg-6">
              <div class="row">
                <div class="col-lg-3">
                  <h3>Support:</h3>
                </div>
                <div class="col-lg-7">
                  <input type="range" style="width: 100%" v-model="support" min="0" max="100" step="1">
                </div>
                <div class="col-lg-2"><input type="number" style="width: 50px" min="0" max="100" v-model="support"></div>
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
                <div class="col-lg-2"><input type="number" style="width: 50px" min="0" max="100" v-model="confiance"></div>
              </div>
            </div>
          </div>
        <div class="row">
        <div class="col-lg-4"></div>
        <div class="btn-check col-lg-4" v-if="!displayResult">
          <b-btn variant="info" class="btn btn-primary btn-lg form-control" id="check-btn" type="button" v-on:click="checkRule">Check a single rule </b-btn>
        </div>
        </div>
        <h3 id="rule-veracity;" v-if="(isTrue == 'true' || isTrue == 'false') && displayResult">This Rule: {{leftAttributes}}
          <i class="fa fa-arrow-right" aria-hidden="true"></i> {{rightAttributes}} is {{isTrue.toUpperCase()}} for this configuration</h3>
            <div class="table-header" v-if="displayResult && isTrue == 'false'">
                <span class="table-title">There are {{summ.counterExampleNB}} counter examples:</span>
                <div class="actions">
                  <a class="waves-effect btn-flat nopadding" href="javascript:undefined" @click="print">
                    <i class="material-icons">print</i>
                  </a>
                  <a class="waves-effect btn-flat nopadding" :href="link" download>
                    <i class="material-icons">description</i>
                  </a>
                </div>
              </div>
        <div class="loader" v-if="results.length == 0 && displayResult"></div>
        <div id="div0" v-if="isTrue == 'false' && displayResult">
            <b-tabs>
                <b-tab title="Condensed view" active>
                    <table class="table table-striped">
                        <thead>
                          <tr>
                            <th>Counter example #</th>
                            <th v-for="valeur in results.header">{{valeur}}</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(result, index) in results.rows">
                            <td>{{Math.trunc(index/rows + 1)}}</td>
                            <td v-for="valeur in result.cels">{{valeur}}</td>
                          </tr>
                        </tbody>
                      </table>
                </b-tab>
                <b-tab title="Complete view" >
                    <table class="table table-striped">
                        <thead>
                          <tr>
                            <th>Counter example #</th>
                            <th v-for="valeur in counterExamples2.header">{{valeur}}</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(result, index) in counterExamples2.rows">
                            <td>{{Math.trunc(index/rows + 1)}}</td>
                            <td v-for="valeur in result.cels">{{valeur}}</td>
                          </tr>
                        </tbody>
                      </table>
                </b-tab>
                <!--b-tab title="view 3">
                    <table class="table table-striped">
                        <thead>
                          <tr>
                            <th>Counter example #</th>
                            <th v-for="valeur in counterExamples3.header">{{valeur}}</th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(result, index) in counterExamples3.rows">
                            <td>{{Math.trunc(index/rows + 1)}}</td>
                            <td v-for="valeur in result.cels">{{valeur}}</td>
                          </tr>
                        </tbody>
                      </table>
                </b-tab-->
              </b-tabs>
            </div>
        </div>
      <div class="col-lg-3" v-if="displayResult == true && summ.length != 0">
          <div class="row">
          <div class="col-lg-1"></div>
          <button class="col-lg-9 btn btn-primary form-control btn-danger" id="actualize" type="button" @click="actualise">Check another rule</button>
          </div>
      <div id="summary">
        <div>
          <b-card no-body="">
            <h4 slot="header">Execution summary</h4>
            <b-card-body>
              <p class="card-text">
                Action: check a rule
              </p>
            </b-card-body>
            <b-list-group flush="">
              <b-list-group-item>Execution time:
                <span>{{summ.timeExecution/1000}}</span> second </b-list-group-item>
              <b-list-group-item>
                <div>
                  <div>Indicated support:
                    <span>{{support}}</span> %
                  </div>
                  <div>Computed support:
                    <span>{{summ.support * 100}}</span> %
                  </div>
                </div>
              </b-list-group-item>
              <b-list-group-item>
                <div>
                  <div>Indicated confidence:
                    <span>{{confiance}}</span> %
                  </div>
                  <div>Computed confidence:
                    <span>{{summ.confidence * 100}}</span> %
                  </div>
                </div>
              </b-list-group-item>
              <b-list-group-item>Rule veracity:
                <span>{{summ.isTrue}}</span>
              </b-list-group-item>
              <b-list-group-item v-if="isTrue == 'false'">Counter examples:
                <span>{{summ.counterExampleNB}}</span>
              </b-list-group-item>
              <b-list-group-item>Query attributes:
                  <span>{{summ.attributesList.length}}<b-btn v-b-toggle.collapse01 variant="info" size="sm">
                      <i class="fa fa-angle-down"></i></b-btn></span>
                  <div>
                      <b-collapse class="mt-2" id="collapse01">
                            <ul class="list-group">
                                <li class="list-group-item my-li" v-b-tooltip v-for="option in summ.attributesList">{{option}}</li>
                            </ul>
                      </b-collapse>
                  </div>
                </b-list-group-item>
            </b-list-group>
            <b-card-footer>RQL</b-card-footer>
          </b-card>
        </div>
      </div>
    </div>
  </div>
    </div>
  
</template>
<script>
  import Autocomplete from 'vue2-autocomplete-js'
  import vSelect from 'vue-select'
  import Multiselect from 'vue-multiselect'
  import Utils from './UtilsMixins'
  export default {
    mixins: [Utils],
    name: 'checkrule',
    components: {
      Autocomplete,
      vSelect,
      Multiselect
    },
    data () {
      return {
        selectedleft: [],
        selectedright: [],
        leftAttributes: '',
        rightAttributes: '',
        IDquery: '',
        isTrue: '',
        display: '',
        sqlQuery: '',
        checked: '',
        rows: 2,
        validated: 0,
        actu: 'false',
        counterExamples1: [],
        counterExamples2: [],
        counterExamples3: [],
        showextend: true,
        showdiminue: false,
        support: 0,
        confiance: 100,
        cpt: 0,
        summ: '',
        link: '',
        displayResult: false
      }
    },

    props: ['queryID', 'isdisplay', 'options'],
    watch: {
      isdisplay (isdisplay) {
        this.display = isdisplay
      },
      queryID (queryID) {
        this.actu = 'false'
        this.displayResult = false
        this.IDquery = queryID
        this.leftAttributes = ''
        this.rightAttributes = ''
        this.selectedleft = []
        this.selectedright = []
        this.isTrue = ''
        this.checked = ''
        this.results = []
        this.validated = 0
      }
    },
    methods: {
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
      },
      checkRule () {
        this.isTrue = ''
        this.displayResult = true
        this.leftAttributes = this.selectedleft.join(' ')
        this.rightAttributes = this.selectedright.join(' ')
        this.link = 'server/excel/' + this.IDquery + '/rulecheck?leftAttributes=' + this.leftAttributes + '&rightAttribute=' + this.rightAttributes + '&support=' + this.support / 100 + '&confidence=' + this.confiance / 100 + '&projectID=' + localStorage.getItem('PROJECTID')
        this.actu = 'false'
        this.results = []
        this.checked = 'true'
        this.$http.get('query/{id}/rulecheck', {params: {id: this.queryID, leftAttributes: this.leftAttributes, rightAttribute: this.rightAttributes, support: this.support / 100, confidence: this.confiance / 100, projectID: localStorage.getItem('PROJECTID')}}).then((response) => {
          this.isTrue = response.data.summary.isTrue
          this.summ = response.data.summary
          this.results = response.data.checkResult.counterExamples3
          this.counterExamples2 = response.data.checkResult.counterExamples
          this.counterExamples3 = response.data.checkResult.counterExamples2
          this.rows = response.data.summary.tupleNB
          this.validated = 1
        }, (response) => {
        })
      },
      actualise () {
        this.displayResult = false
        this.results = []
      },
      nextt () {
        this.results = this.counterExamples1
      },
      precc () {
        this.results = this.counterExamples3
      },
      getData (obj) {
        console.log(obj)
      }
    }
  }
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
  .table-title {
		font-size: 13pt;
	}
  .counterExample-nb{
    margin-bottom:0%;
  }
  .checkrule {
    margin-top: 0%;
  }
  h1,
  h2 {
    font-weight: normal;
  }
  #fleche {
    margin: auto;
  }

  #div0 {
    max-height: 400px;
    overflow: auto;
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
.zoom{
    margin-left:5%;
    display:inline-block;
  }
.zoom div:hover img {
	-webkit-transform: scale(1.3);
	transform: scale(1.3);
}
span{
    font-weight:bold
  }
#check-btn{
  margin-top: 10%; 
  height:40pt;
}
h3 i {
  margin-top: 0px; 
  color:#3498db;
}
  .card-text{
    font-weight: bold;
  }
  .btn-check{
    margin-top: 2%;
  }
  .rule-veracity{
    margin-top: 0%;
  }
  #summary{
    margin-top:2%;
    margin-right:5%;
    margin-left:0%;
    max-height: 500px;
    overflow: auto
    
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
  h3{
    margin-top:0%
  }
  #params{
    margin-top: 2%;
  }
  #actualize{
  }
  .my-li{
    font-size:8pt; 
    font-weight:bolder;
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px ;
  }
</style>
