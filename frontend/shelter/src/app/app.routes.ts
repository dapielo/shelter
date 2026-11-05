import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Login } from './user/login/login';
import { Register } from './user/register/register';
import { PetInfo } from './pet/pet-info/pet-info';
import { PendingAdoptions } from './adoption/pending-adoptions/pending-adoptions';
import { SearchForm } from './pet/search-form/search-form';
import { PetForm } from './pet/pet-form/pet-form';
import { AdoptionHistory } from './adoption/adoption-history/adoption-history';
import { Fosterhome } from './fosterhome/fosterhome/fosterhome';

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
    {path:"adoption/history", component:AdoptionHistory},
    {path:"fosterhome/:fosterhomeId/pet/edit/:petId", component:PetForm}
];
