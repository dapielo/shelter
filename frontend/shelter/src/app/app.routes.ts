import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { PetInfo } from './components/pet-info/pet-info';
import { PendingAdoptions } from './components/pending-adoptions/pending-adoptions';

export const routes: Routes = [
    // Ojo con las rutas, nunca empiezan por "/"
    {path:"",component:Home},
    {path:"home",component:Home},
    {path:"login",component:Login},
    {path:"register",component:Register},
    {path:"pet/:id",component:PetInfo}, // El :id, hace que esa parte de la ruta sea una variable para poder capturarla en el componente que lo necesite
    {path:"adoptions/pending",component:PendingAdoptions}
];
