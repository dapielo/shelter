import { Component, input } from '@angular/core';
import { PetDTO } from '../../model/PetDTO';
import { PetCard } from '../pet-card/pet-card';

@Component({
  selector: 'app-pet-grid',
  imports: [PetCard],
  templateUrl: './pet-grid.html',
  styleUrl: './pet-grid.css',
})
export class PetGrid {

  pets = input.required<PetDTO[]>();
}
