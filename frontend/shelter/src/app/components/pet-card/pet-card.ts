import { Component, computed, inject, input } from '@angular/core';
import { PetService } from '../../services/pet-service';
import { PetDTO } from '../../model/PetDTO';
import { SpeciesLabel } from '../../model/SpeciesLabels';
import { AgeGroupLabel } from '../../model/AgeGroupLabel';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment.development';

@Component({
  selector: 'app-pet-card',
  imports: [],
  templateUrl: './pet-card.html',
  styleUrl: './pet-card.css',
})

export class PetCard {

    router = inject(Router);
    // input es un signal que recibe un parametro que le pasa el componente padre a traves de la plantilla html, required si es olbigatorio
    pet = input.required<PetDTO>();
    
    petFormatted = computed(() => {
      // hacemos un cambio en species (campo no nulo) y ageGroup (campo opcional) y usamos este objeto formateado para renderizar
      return {...this.pet(), 
        species:SpeciesLabel[this.pet().species], 
        ageGroup:AgeGroupLabel[this.pet().ageGroup ?? ''] ?? 'desconocido'}
    });

    imgRoute = computed<string>(() => {
      return this.petFormatted().imagePath ? `${environment.api}/image/${this.petFormatted().imagePath}` : `images/default_photo.png` ;
    });

    toPetInfo() {
      this.router.navigate(["/pet",`${this.pet().id}`]);
    }
}
