import { Component, inject, OnInit, signal } from '@angular/core';
import { ProvinceLabel } from '../../fosterhome/dto/ProvinceLabel';
import { AgeGroupLabel } from '../../adoption/dto/AgeGroupLabel';
import { SpeciesLabel } from '../dto/SpeciesLabels';
import { PetDTO } from '../dto/PetDTO';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PetGrid } from '../pet-grid/pet-grid';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { PetService } from '../pet-service';

@Component({
  selector: 'app-search-form',
  imports: [ReactiveFormsModule, PetGrid],
  templateUrl: './search-form.html',
  styleUrl: './search-form.css',
})
export class SearchForm implements OnInit {

  // convertimos los mapas en arrays de literales para construir los select en la vista
  provinces = Object.entries(ProvinceLabel).map(([value,label]) => ({value, label}));
  ageGroups = Object.entries(AgeGroupLabel).map(([value,label]) => ({value, label}));
  speciesList = Object.entries(SpeciesLabel).map(([value,label]) => ({value, label}));

  petService = inject(PetService);
  route = inject(ActivatedRoute);

  pets = signal<PetDTO[]>([]);
  message = signal<string>("Selecciona los filtros de búsqueda y pulsa en Buscar");
  errorMessage = signal<string>('');
  speciesParam = signal<string>('');

  searchForm = new FormGroup({
    // la provincia es el unico campo obligatorio
    province : new FormControl('',[
      Validators.required
    ]),

    species: new FormControl(''),

    ageGroup: new FormControl('')
  });

  ngOnInit(): void {
    
    this.route.queryParamMap.subscribe({
      next: (params) => {
        this.speciesParam.set(params.get('species') ?? ''); // el get me devuelve null si no existe ese param, tengo que gestionarlo o el signal puede ser null o le doy un valor por defecto

        // si no venia nada en el species vovlemos, sino se ejecuta la llamada de abajo
        if (this.speciesParam() == '') {
          return;
        }

        this.petService.getAll().subscribe({
          next: (data) => {
            this.pets.set(data.filter(pet => pet.species == this.speciesParam()));
          },
          error: (err: HttpErrorResponse) => {
            if (err.status === 0) {
              this.errorMessage.set('No se ha podido establecer la conexion, comprueba la conexion a internet');
            } else {
              this.errorMessage.set('Error en el servidor, vuelve a intentaro mas tarde.')
            }
          }
        });
      }
    })
  }

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
