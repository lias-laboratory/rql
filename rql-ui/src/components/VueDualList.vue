<template>
  <div class='vue-dual-list'>
    <div class="row">
      <div class="col-lg-5">
        <h4>Available</h4>
        <div class='list' v-bind:class="options.resizeBox">
          <ul class='list-group'>
            <li v-for='item in filtering' class="my-li" :key="item.name" >
              <div class="row" v-on:click='transferToRight(options.items.indexOf(item))'>
                <div class="col-lg-10">
              <a href='#'  v-bind:style="{ color: options.colorItems || '#1E90FF' }">
                {{ item.name }}</a>
              </div>
              <div class="col-lg-2">
                  <i class="fa fa-chevron-right" v-on:click='transferToLeft(-1)' style="margin:auto"></i>
              </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
      <div class="col-lg-2">
        <div class="row" style="margin-top:60%">
          <i class="fa fa-forward fa-2x" v-on:click='transferToRight(options.items.indexOf(item))' style="margin:auto"></i>
          </div>
        <div class="row" style="margin-top:30%">          
            <i class="fa fa-backward fa-2x" v-on:click='transferToLeft(-1)' style="margin:auto"></i>
        </div>
      </div>
      <div class="col-lg-5">
        <h4>Selected</h4>
        <div class='list' v-bind:class="options.resizeBox">
          <ul class='list-group' >
            <li v-for='item in options.selectedItems' class="my-li" :key="item.name">
              <div class="row"  v-on:click='transferToLeft(options.selectedItems.indexOf(item))'>
                  <div class="col-lg-1">
                      <i class="fa fa-chevron-left" v-on:click='transferToLeft(-1)' style="margin:auto"></i>
                  </div>
                  <div class="col-lg-10">
                      <a href='#'  v-bind:style="{ color: '#009688'}">
                          {{ item.name }}
                        </a>
                </div>
                </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
  .vue-dual-list .list {
    border: 1px solid #999;
    border-radius: 4px;
    padding: 10px;
    padding-top: 0%;
    overflow-y: scroll;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
  }

  .vue-dual-list .xs {
    height: 150px;
  }

  .vue-dual-list .md {
    height: 225px;
  }

  .vue-dual-list .lg {
    height: 350px;
  }

  .vue-dual-list .xl {
    height: 500px;
  }

  .vue-dual-list ul.pd {
    padding-left: 12px;
  }

  .my-li{
    margin: 0px 0px 0px 0px;
    padding: 0px 0px 0px 0px ;
  }
</style>

<script>
  export default {
    name: 'vue-dual-list',
    props: {
      'options': {
        type: Object,
        default: {},
        required: true
      }
    },
    data: function () {
      return {
        search: '',
        item: {}
      }
    },
    methods: {
      transferToRight: function (index) {
        this.search = ''
        if (index >= 0) {
          this.options.selectedItems.push(this.options.items[index])
          this.options.items.splice(index, 1)
        } else {
          for (var cont = 0; cont < this.options.items.length; cont++) {
            this.options.selectedItems.push(this.options.items[cont])
          }

          this.options.items.splice(0, this.options.items.length)
        }
      },
      transferToLeft: function (index) {
        this.search = ''
        if (index >= 0) {
          this.options.items.push(this.options.selectedItems[index])
          this.options.selectedItems.splice(index, 1)
          return
        }
        for (var cont = 0; cont < this.options.selectedItems.length; cont++) {
          this.options.items.push(this.options.selectedItems[cont])
        }
        this.options.selectedItems.splice(0, this.options.selectedItems.length)
      }
    },
    computed: {
      filtering: function () {
        if (this.search) {
          return this.options.items.filter((item) => {
            return item.name.toLowerCase().indexOf(this.search) !== -1
          })
        }
        return this.options.items
      }
    }
  }
</script>