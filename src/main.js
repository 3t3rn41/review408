import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import VueCropper from 'vue-cropperjs';  
import 'cropperjs/dist/cropper.css'; // 导入cropperjs的样式 
import md5 from 'js-md5';
const app=createApp(App)
app.use(Antd)
app.component('vue-cropper', VueCropper);  
try{
app.use(md5)
}
catch
{
    
}
app.mount('#app')
