import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';

export const routes: Routes = [
    // Ojo con las rutas, nunca empiezan por "/"
    {path:"",component:Home},
    {path:"home",component:Home},
    {path:"login",component:Login},
    {path:"register",component:Register}
];
