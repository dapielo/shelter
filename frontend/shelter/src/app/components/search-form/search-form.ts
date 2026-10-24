import { Component, inject, OnInit, signal } from '@angular/core';
import { ProvinceLabel } from '../../model/ProvinceLabel';
import { AgeGroupLabel } from '../../model/AgeGroupLabel';
import { SpeciesLabel } from '../../model/SpeciesLabels';
import { PetService } from '../../services/pet-service';
import { PetDTO } from '../../model/PetDTO';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PetGrid } from '../pet-grid/pet-grid';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-search-form',
  imports: [ReactiveFormsModule, PetGrid],
  templateUrl: './search-form.html',
  styleUrl: './search-form.css',
})
export class SearchForm{

  // convertimos los mapas en arrays de literales para construir los select en la vista
  provinces = Object.entries(ProvinceLabel).map(([value,label]) => ({value, label}));
  ageGroups = Object.entries(AgeGroupLabel).map(([value,label]) => ({value, label}));
  speciesList = Object.entries(SpeciesLabel).map(([value,label]) => ({value, label}));

  petService = inject(PetService);

  pets = signal<PetDTO[]>([]);
  message = signal<string>("Selecciona los filtros de búsqueda y pulsa en Buscar");
  errorMessage = signal<string>('');

  searchForm = new FormGroup({
    // la provincia es el unico campo obligatorio
    province : new FormControl('',[
      Validators.required
    ]),

    species: new FormControl(''),

    ageGroup: new FormControl('')
  });

  onSubmit() {
    
    if (!this.searchForm.valid) {
      this.errorMessage.set("Tienes que indicar la provincia.");
      return;
    }

    const speciesFilter = this.searchForm.get("species")?.value ?? '' ;
    const ageGroupFilter = this.searchForm.get("ageGroup")?.value ?? '';

    this.petService.getPetsByProvince(this.searchForm.get("province")!.value!).subscribe({
      next: (data: PetDTO[]) => {
        this.pets.set(data
          .filter(p => speciesFilter ? p.species == speciesFilter : true)
          .filter(p => ageGroupFilter ? p.ageGroup == ageGroupFilter : true));

      // Si la lista viene vacia lo indicamos en el mensaje y borramos el mensaje e error si estaba 
      if (this.pets().length === 0) {
        this.message.set("No hay resultados que coincidan con la búsqueda.");
        this.errorMessage.set('');
      }

    },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set("Error: comprueba si tienes conexion a internet")
        } else {
          this.errorMessage.set(err.error.message);
        }
      }
    });

    
  }
}
