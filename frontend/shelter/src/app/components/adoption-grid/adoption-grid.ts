import { Component, inject, input, output } from '@angular/core';
import { AdoptionDTO } from '../../model/AdoptionDTO';
import { AdoptionCard } from '../adoption-card/adoption-card';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-adoption-grid',
  imports: [AdoptionCard],
  templateUrl: './adoption-grid.html',
  styleUrl: './adoption-grid.css',
})
export class AdoptionGrid {

  auth = inject(AuthService);

  // listado de adopciones que recibe de su padre
  adoptions = input.required<AdoptionDTO[]>();
  // el padre indica tambien si es el listado como dueño o como adoptante
  isOwner = input.required<boolean>();

  // Este componente recibe datos de su hijo
  acceptAdoption = output<number>();
  declineAdoption = output<number>();
  deleteAdoption = output<number>();
}
