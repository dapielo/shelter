import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../user/auth-service';
import { Router } from '@angular/router';
import { FosterHomeService } from '../foster-home-service';
import { FosterHomeDTO } from '../dto/FosterHomeDTO';
import { HttpErrorResponse } from '@angular/common/http';
import { FosterhomeCard } from '../../fosterhome/fosterhome-card/fosterhome-card';
import { PetService } from '../../pet/pet-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProvinceLabel } from '../dto/ProvinceLabel';

@Component({
  selector: 'app-fosterhome',
  imports: [FosterhomeCard, ReactiveFormsModule],
  templateUrl: './fosterhome.html',
  styleUrl: './fosterhome.css',
})
export class Fosterhome implements OnInit{

  auth = inject(AuthService);
  router = inject(Router);
  fosterHomeService = inject(FosterHomeService);
  petService = inject(PetService);

  fosterHomes = signal<FosterHomeDTO[]>([]);
  errorMessage = signal<string>('');
  loading = signal<boolean>(true);
  showAddForm = signal<boolean>(false);

  provinceSelect = Object.entries(ProvinceLabel).map(([v, l]) => ({value:v,label:l}));

  fosterHomeForm : FormGroup = new FormGroup({

    name: new FormControl('',Validators.required),

    capacity: new FormControl(1,[
      Validators.required,
      Validators.min(1)
    ]),

    province: new FormControl('',Validators.required)
  })

  ngOnInit() {

    this.fosterHomeService.getFosterHomes().subscribe({
      next: data => {
        this.fosterHomes.set([...data]);
        this.loading.set(false);
        this.showAddForm.set(true);
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 403) {
          this.auth.logout();
          this.router.navigate(["login"]);
        } else {
          this.loading.set(false);
          this.errorMessage.set(err.error.message);
        }
      }
    });
  }

  // capturamos el evento con el valor que emite el output de los botones de las tarjetas
  onDeletePet(petId: number, fosterhomeId: number) {

    if (confirm("Seguro que quieres borrar?")) {

      this.petService.deletePet(petId).subscribe({
        next: () => {
          // actualizamos las casas de acoida, de cada una de ellas, expandimos el objeto y reasignamos pets filtrando si tiene un pet con el id de la que borramos
          this.fosterHomes.update(list => list.map(fosterHome => ({...fosterHome, pets: fosterHome.pets?.filter(p => p.id !== petId)})));
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("Error de conexion, vuelvelo a intentar");
          } else {
            this.errorMessage.set(err.error.message);
          }
        }
      });
    }
  }

  onDeleteFosterHome(fosterHomeId: number) {
    
    if (confirm("Seguro que quieres borrar el hogar? Se perderan las publicaciones de todas sus mascotas")) {

      this.fosterHomeService.deleteFosterHome(fosterHomeId).subscribe({
        next: () => { this.fosterHomes.set(this.fosterHomes().filter(f => f.id !== fosterHomeId)) },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("Error de conexion, vuelvelo a intentar");
          } else {
            this.errorMessage.set(err.error.message);
          }
        }
      });
    }
  }

  // Pequeño formulario de creacion de casas de acogida
  onCreateFosterHome() {

    if (!this.fosterHomeForm.valid) {
      this.errorMessage.set("Los datos del hogar no son correctos");
      return;
    }
    this.errorMessage.set('');

    this.fosterHomeService.createFosterHome(this.fosterHomeForm.getRawValue()).subscribe({
      next: (data: FosterHomeDTO) => {
        this.fosterHomes.update(list => [...list,data]);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set('Error de conexion, revisa si tienes internet');
        } else {
          this.errorMessage.set(err.error.message);
        }
      }
    })
  }

}
