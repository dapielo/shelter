import { Component, inject, input, OnInit, signal, Signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpeciesLabel } from '../../model/SpeciesLabels';
import { AgeGroupLabel } from '../../model/AgeGroupLabel';
import { PetService } from '../../services/pet-service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth-service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-pet-form',
  imports: [ReactiveFormsModule],
  templateUrl: './pet-form.html',
  styleUrl: './pet-form.css',
})
export class PetForm implements OnInit{

  petService: PetService = inject(PetService);
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);

  speciesLabels = Object.entries(SpeciesLabel).map(([value,label]) => ({value: value, label: label}));
  ageGroupLabels = Object.entries(AgeGroupLabel).map(([value,label]) => ({value:value, label:label}));

  // el id nos viene en la ruta, lo capturamos con input
  fosterhomeId = input.required<number>();

  petForm = new FormGroup({

    name: new FormControl('',Validators.required),
    species: new FormControl('',Validators.required),
    breed: new FormControl(''),
    ageGroup: new FormControl(''),
    info: new FormControl(''),
  });

  image = signal<File | null>(null);
  errorMessage = signal<string>('');

  onImageSelected(target: HTMLInputElement) {
    this.image.set(target.files?.[0] ?? null); // el value del input es el string con la ruta, la imagen se captura con la propiedad files que es un array
  }

  ngOnInit(): void {
    if (!this.authService.isLogged()) {
      this.router.navigate(["login"]);
    }
  }

  onReset() {
    this.petForm.reset();
    this.image.set(null);
  }

  onSubmit() {

    if (!this.petForm.valid) {
      this.errorMessage.set('El nombre y la especie son obligatorios');
      return;
    }

    // Como puede llevar imagen tenemos que capturar los campos con un formData.
    const formData: FormData = new FormData();
    const values = this.petForm.getRawValue();

    formData.append("name",values.name!); // ! poruqe sabemos que viene, es obligatorio
    formData.append("species",values.species!); // igual que con name
    formData.append("fosterHomeId",this.fosterhomeId().toString());
    values.breed && formData.append("breed",values.breed); // && por cortocircuito si lo de la izquierda es falsy ya no ejecuta lo de la derecha
    values.ageGroup && formData.append("ageGroup",values.ageGroup);
    values.info && formData.append("info",values.info);
    this.image() && formData.append("image",this.image()!); // al ser una funcion TypeScript no puede deducir que la segunda llamada va a tener el mismo resultado que la primera

    this.petService.createPet(formData).subscribe({

      next: () => {this.router.navigate(["fosterhome"])},
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set("No tienes conexion a internet, vuelve a intentarlo");
          return;
        }
        if (err.status === 403) {
          this.authService.logout();
          this.router.navigate(["login"]);
          return 
        }
        this.errorMessage.set(err.error?.message ?? "No se ha podido registar la mascota");
      }
    });

  }
}
