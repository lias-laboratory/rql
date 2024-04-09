<template >
  <div class="login">
	<div class="container">
		<div  class="wrapper">
			<form id="content" class="form-signin" name="Login_Form" v-on:submit.prevent="onSubmit">
          <div id="contentchild" class="row" v-if="loading">
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
		    	<h3 class="form-signin-heading">Welcome to RQL</h3>
			  	<hr class="colorgraph"><br>
			  	<input @change="onTape = false" class="form-control form-control-login" name="Username" type="text" v-model="credentials.username" placeholder="Username" required autofocus>
			  	<input @change="onTape = false" class="form-control form-control-login" name="Password" type="password" v-model="credentials.password" placeholder="Password" required>
        <button id="mybutton" class="btn btn-lg btn-primary btn-block" type="submit" v-bind:disabled="!isValid">Login</button>
        <div v-if="errorMessage == 'Unauthorized'">
        <b-alert  style="margin-bottom:0%; margin-top:2%" :show="onTape" variant="danger">
                                        <p>Invalid login or password</p>
        </b-alert>
    </div>
      </form>
		</div>
    </div>
</div>
</template>

<script>
import Utils from './UtilsMixins'
export default {
  mixins: [Utils],
  data () {
    return {
      credentials: {
        username: '',
        password: ''
      },
      loading: false,
      showError: false,
      errorMessage: '',
      dismissCountDown: 0,
      onTape: false,
      dismissSecs: 10
    }
  },
  computed: {
    isValid () {
      return this.credentials.username !== '' && this.credentials.password !== ''
    }
  },
  methods: {
    onSubmit () {
      this.loading = true
      this.errorMessage = ''
      this.$http.post('authentication/login', JSON.stringify(this.credentials)).then((response) => {
        localStorage.setItem('TOKEN', response.bodyText)
        localStorage.setItem('USERNAME', this.credentials.username)
        localStorage.removeItem('PROJECTID')
        this.loading = false
        this.$router.push('/project')
      }, (response) => {
        localStorage.removeItem('TOKEN')
        this.loading = false
        this.onTape = true
        this.errorMessage = response.statusText
      })
    }
  }
}
</script>

<style>
    #content {
        position: relative;
        opacity: 0.93;

    }

    #content #contentchild {
        position: absolute;
        top: 0px;
        width: 100%
    }
    .login  .wrapper2 {
        width: 100%;
        height: 2px;
        left: 0;
        right: 0;
        bottom: 0;
        top: 0;
        margin-top: 2%;
        margin-bottom: 0%;
    }

    .login .loader2 {
        height: 100%;
        display: flex;
        -webkit-transform: translateZ(0);
        transform: translateZ(0);
    }

    .login .loader2 div {
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
.wrapper {    
	margin-top: 80px;
	margin-bottom: 20px;
}
body {
    background: url(../assets/bg.jpg)repeat center 0px ; 
}
.form-signin {
  max-width: 420px;
  padding: 30px 38px 66px;
  margin: 0 auto;
  background-color: #eee;
  border: 3px solid rgba(0,0,0,0.1);  
  }

.form-signin-heading {
  text-align:center;
  margin-bottom: 30px;
}

.form-control-login {
  font-size: 16px;
  padding: 10px;
  margin-top: 10px;
}

input[type="text"] {
  margin-bottom: 0px;
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

input[type="password"] {
  margin-bottom: 20px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}

.colorgraph {
  height: 7px;
  border-top: 0;
  background: #c4e17f;
  border-radius: 5px;
  background-image: -webkit-linear-gradient(left, #c4e17f, #c4e17f 12.5%, #f7fdca 12.5%, #f7fdca 25%, #fecf71 25%, #fecf71 37.5%, #f0776c 37.5%, #f0776c 50%, #db9dbe 50%, #db9dbe 62.5%, #c49cde 62.5%, #c49cde 75%, #669ae1 75%, #669ae1 87.5%, #62c2e4 87.5%, #62c2e4);
  background-image: -moz-linear-gradient(left, #c4e17f, #c4e17f 12.5%, #f7fdca 12.5%, #f7fdca 25%, #fecf71 25%, #fecf71 37.5%, #f0776c 37.5%, #f0776c 50%, #db9dbe 50%, #db9dbe 62.5%, #c49cde 62.5%, #c49cde 75%, #669ae1 75%, #669ae1 87.5%, #62c2e4 87.5%, #62c2e4);
  background-image: -o-linear-gradient(left, #c4e17f, #c4e17f 12.5%, #f7fdca 12.5%, #f7fdca 25%, #fecf71 25%, #fecf71 37.5%, #f0776c 37.5%, #f0776c 50%, #db9dbe 50%, #db9dbe 62.5%, #c49cde 62.5%, #c49cde 75%, #669ae1 75%, #669ae1 87.5%, #62c2e4 87.5%, #62c2e4);
  background-image: linear-gradient(to right, #c4e17f, #c4e17f 12.5%, #f7fdca 12.5%, #f7fdca 25%, #fecf71 25%, #fecf71 37.5%, #f0776c 37.5%, #f0776c 50%, #db9dbe 50%, #db9dbe 62.5%, #c49cde 62.5%, #c49cde 75%, #669ae1 75%, #669ae1 87.5%, #62c2e4 87.5%, #62c2e4);
}
#mybutton{
  background-color:#009688;
  border-color: #009688
}
</style>