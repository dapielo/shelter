import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { PetService } from '../../pet/pet-service';
import { PetDTO } from '../dto/PetDTO';
import { SpeciesLabel } from '../dto/SpeciesLabels';
import { AgeGroupLabel } from '../../adoption/dto/AgeGroupLabel';
import { environment } from '../../../environments/environment';
import { AdoptionService } from '../../adoption/adoption-service';
import { AuthService } from '../../user/auth-service';

@Component({
  selector: 'app-pet-info',
  imports: [],
  templateUrl: './pet-info.html',
  styleUrl: './pet-info.css',
})
export class PetInfo implements OnInit {

  // Inyeccion de dependencias
  auth = inject(AuthService);
  router = inject(Router);
  petService = inject(PetService);
  adoptionService = inject(AdoptionService);

  // input es el que se usa para recibir parametros desde el padre, pero en este caso recibimos el :id de la ruta (tienen que coincidir en nombre)
  // recordar que es un signal, se llama como funncion ()
  id = input.required<string>();
  pet = signal<PetDTO | null>(null);
  petFormatted = computed(() => {
    return {...this.pet(), 
      species:SpeciesLabel[this.pet()?.species ?? ''] ?? 'desconocido', 
      ageGroup:AgeGroupLabel[this.pet()?.ageGroup ?? ''] ?? 'desconocido'}
  })
  errorMessage = signal<string>('');
  imageRoute = computed<string>(() => {
    return this.petFormatted().imagePath ? `${environment.api}/image/${this.petFormatted().imagePath}` : '/images/default_photo.png'
  });
  isOwner = computed(() => this.pet()?.ownerId === this.auth.loadUser()?.id);

  ngOnInit() {

    this.petService.getPet(Number(this.id())).subscribe({
      next: (data) => {
        this.pet.set(data);
      },
      error: (err) => {
        this.errorMessage.set(err.error.message);
      }
    });
  }

  initiateAdoption() {

    if (!this.auth.isLogged()) {
      this.router.navigate(["login"],{queryParams: {error : "Para poder iniciar una adopcion tienes que iniciar sesion"}});
      return;
    }
    
    this.adoptionService.iniciateAdoption(Number(this.id())).subscribe({
      next: (data) => {
        alert("La adopcion se ha iniciado, ahora tienes que esperar a recibir una respuesta por parte del hogar de acogida. Se te va a redirigir a tu listado de adociones.")
        this.router.navigate(["/adoption","pending"]);
      },
      error: (err) => {
        this.errorMessage.set("No se ha podido iniciar la adopcion: " + err.error.message);
      }
    });
  }
}
