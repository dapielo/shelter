import { Component, computed, input, output } from '@angular/core';
import { FosterHomeDTO } from '../../model/FosterHomeDTO';
import { ProvinceLabel } from '../../model/ProvinceLabel';
import { PetCard } from '../pet-card/pet-card';

@Component({
  selector: 'app-fosterhome-card',
  imports: [PetCard],
  templateUrl: './fosterhome-card.html',
  styleUrl: './fosterhome-card.css',
})
export class FosterhomeCard {

  // Lo recibe en la plantlla de su padre
  fosterHome = input.required<FosterHomeDTO>();
  provinceFormated = computed(() => ProvinceLabel[this.fosterHome().province]);

  // Enviamos hacia arriba el evento al clickar en uno de los botones
  editPet = output<number>();
  deltePet = output<number>();

  deleteFosterHome = output<number>();
}
