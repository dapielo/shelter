import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { PetService } from '../../services/pet-service';
import { PetDTO } from '../../model/PetDTO';
import { SpeciesLabel } from '../../model/SpeciesLabels';
import { AgeGroupLabel } from '../../model/AgeGroupLabel';
import { environment } from '../../../environments/environment.development';
import { AdoptionService } from '../../services/adoption-service';

@Component({
  selector: 'app-pet-info',
  imports: [],
  templateUrl: './pet-info.html',
  styleUrl: './pet-info.css',
})
export class PetInfo implements OnInit {

  // input es el que se usa para recibir parametros desde el padre, pero en este caso recibimos el :id de la ruta (tienen que coincidir en nombre)
  // recordar que es un signal, se llama como funncion ()
  id = input.required<string>();
  router = inject(Router)
  petService = inject(PetService);
  adoptionService = inject(AdoptionService);
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
    
    this.adoptionService.iniciateAdoption(Number(this.id())).subscribe({
      next: (data) => {
        alert("La adopcion se ha iniciado, ahora tienes que esperar a recibir una respuesta por parte del hogar de acogida. Se te va a redirigir a tu listado de adociones.")
        this.router.navigate(["adoptions","pending"]);
      },
      error: (err) => {
        alert("No se ha podido iniciar la adopcion: " + err.error.message);
      }
    });
  }
}
