import { Component, inject, input, signal } from '@angular/core';
import { PetGrid } from '../pet-grid/pet-grid';
import { PetService } from '../../services/pet-service';
import { PetDTO } from '../../model/PetDTO';

@Component({
  selector: 'app-home',
  imports: [PetGrid],
  templateUrl: './home.html',
  styleUrl: './home.css',
})

export class Home {

  petService = inject(PetService);
  // Tiene que ser un signal, para que la vista cambie automaticamente cuando se reciben los datos en el ngOnInit() y se almacenan aqui
  pets = signal<PetDTO[]>([]);
  error = signal<string | null>(null);

  // ES una directiva de angular que ejecuta el código cuando el componente se crea
  ngOnInit() {

    // hacemos peticion de los datos al servidor
    this.petService.getVeteranPets().subscribe({
      next: (data) => {
        this.pets.set([...data]);
      },
      error: (err) => {
        this.error = err.error.message
      }
    });
  }
}
