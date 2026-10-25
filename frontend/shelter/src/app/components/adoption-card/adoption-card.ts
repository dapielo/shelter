import { Component, computed, inject, input, output } from '@angular/core';
import { PetDTO } from '../../model/PetDTO';
import { AdoptionDTO } from '../../model/AdoptionDTO';
import { SpeciesLabel } from '../../model/SpeciesLabels';
import { AgeGroupLabel } from '../../model/AgeGroupLabel';
import { environment } from '../../../environments/environment.development';
import { Router, RouterLink } from '@angular/router';
import { AdoptionStatusLabel } from '../../model/AdoptionStatusLabel';

@Component({
  selector: 'app-adoption-card',
  imports: [],
  templateUrl: './adoption-card.html',
  styleUrl: './adoption-card.css',
})
export class AdoptionCard {

  // La informacion de la adopcion y de la mascota nos la tiene que pasar el padre del componente
  adoption = input.required<AdoptionDTO>();
  // propiedad que le pasa el padre para saber si la mascota es suya o no (determina los botones)
  isOwner = input.required<boolean>();
  adoptionStatus = computed(() => AdoptionStatusLabel[this.adoption().status])
  species = computed(() => SpeciesLabel[this.adoption().species]);

  image = computed(() => {
    return this.adoption().imagePath ? `${environment.api}/image/${this.adoption().imagePath}` : 'images/default_photo.png';
  });

  // Estos datos son para enviar hacia arriba cuando un usuario acepta, rechaza o borra una adopcion. output crea un evento con el nombre
  // de la variable que puede ser capturado en la plantilla padre de esta como (acceptAdoption)="..."
  acceptAdoption = output<number>();
  declineAdoption = output<number>();
  deleteAdoption = output<number>();

  router = inject(Router);
  openPetCard() {
    this.router.navigate(["pet","info",this.adoption().petId]);
  }
}
