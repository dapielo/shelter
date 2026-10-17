import { Component, computed, inject, input } from '@angular/core';
import { PetService } from '../../services/pet-service';
import { PetDTO } from '../../model/PetDTO';
import { SpeciesLabel } from '../../model/SpeciesLabels';
import { AgeGroupLabel } from '../../model/AgeGroupLabel';
import { Router } from '@angular/router';

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
      const pet = this.pet();
      // hacemos un cambio en species (campo no nulo) y ageGroup (campo opcional) y usamos este objeto formateado para renderizar
      return {...pet, species:SpeciesLabel[pet.species], ageGroup:AgeGroupLabel[pet.ageGroup ?? '']}
    });

    // el campo de imagen no es obligatorio asi que le metemos undefined tambien
    imgRoute = computed<string | null>(() => {
      return this.petFormatted().imagePath ? `http://127.0.0.1:8080/shelter/image/${this.petFormatted().imagePath}` : `images/shelter_icon.png` ;
    });

    toPetInfo() {
      this.router.navigate(["/pet",`${this.pet().id}`]);
    }
}
