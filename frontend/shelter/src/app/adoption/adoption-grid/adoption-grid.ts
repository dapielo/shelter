import { Component, inject, input, output } from '@angular/core';
import { AdoptionDTO } from '../dto/AdoptionDTO';
import { AdoptionCard } from '../../adoption/adoption-card/adoption-card';
import { AuthService } from '../../user/auth-service';

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
  buttonType = input.required<string>();

  // Este componente recibe datos de su hijo
  acceptAdoption = output<number>();
  declineAdoption = output<number>();
  deleteAdoption = output<number>();
}
