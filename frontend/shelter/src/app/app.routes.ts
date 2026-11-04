import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { PetInfo } from './components/pet-info/pet-info';
import { PendingAdoptions } from './components/pending-adoptions/pending-adoptions';
import { Fosterhome } from './components/fosterhome/fosterhome';
import { SearchForm } from './components/search-form/search-form';
import { PetForm } from './components/pet-form/pet-form';
import { AdoptionHistory } from './components/adoption-history/adoption-history';

export const routes: Routes = [
    // Ojo con las rutas, nunca empiezan por "/"
    {path:"",component:Home},
    {path:"home",component:Home},
    {path:"login",component:Login},
    {path:"register",component:Register},
    {path:"pet/info/:id",component:PetInfo}, // El :id, hace que esa parte de la ruta sea una variable para poder capturarla en el componente que lo necesite
    {path:"adoption/pending",component:PendingAdoptions},
    {path:"fosterhome",component:Fosterhome},
    {path:"pet/search",component:SearchForm},
    {path:"fosterhome/:fosterhomeId/pet/create",component: PetForm},
    {path:"adoption/history", component:AdoptionHistory}
];
