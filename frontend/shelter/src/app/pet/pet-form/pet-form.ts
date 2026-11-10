import { Component, inject, input, OnInit, signal, Signal, effect, computed } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SpeciesLabel } from '../dto/SpeciesLabels';
import { AgeGroupLabel } from '../../adoption/dto/AgeGroupLabel';
import { PetService } from '../../pet/pet-service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../user/auth-service';
import { PetDTO } from '../dto/PetDTO';
import { environment } from '../../../environments/environment';
import { FosterHomeService } from '../../fosterhome/foster-home-service';
import { FosterHomeDTO } from '../../fosterhome/dto/FosterHomeDTO';

@Component({
  selector: 'app-pet-form',
  imports: [ReactiveFormsModule],
  templateUrl: './pet-form.html',
  styleUrl: './pet-form.css',
})
export class PetForm implements OnInit{

  petService: PetService = inject(PetService);
  authService: AuthService = inject(AuthService);
  fosterhomeService: FosterHomeService = inject(FosterHomeService);
  router: Router = inject(Router);

  speciesLabels = Object.entries(SpeciesLabel).map(([value,label]) => ({value: value, label: label}));
  ageGroupLabels = Object.entries(AgeGroupLabel).map(([value,label]) => ({value:value, label:label}));

  // el id nos viene en la ruta, lo capturamos con input
  currentFosterhomeId = input.required<number>();
  // el id de la masctota n os viene en la ruta si venimos desde el boton de editar
  petId = input<number>();
  // aqui van los datos de la mascota que estamos editando
  existentPet = signal<PetDTO | null>(null);
  // Si se carga una mascota, alamcenamos la ruta de su imagen para mostrarla
  imageServerPath = computed(() => {
    return this.existentPet()?.imagePath != null ? `${environment.api}/image/${this.existentPet()!.imagePath}` : '';
  });
  // Aqui vamos a guardar las casas de acogida por si quieremos cambar la mascota de hogar
  userFosterHomes = signal<FosterHomeDTO[]>([]);

  petForm = new FormGroup({

    name: new FormControl('',Validators.required),
    species: new FormControl('',Validators.required),
    breed: new FormControl(''),
    ageGroup: new FormControl(''),
    info: new FormControl(''),
    fosterhomeId: new FormControl('')
  });

  imageClientPath = signal<File | null>(null);
  errorMessage = signal<string>('');

  onImageSelected(target: HTMLInputElement) {
    this.imageClientPath.set(target.files?.[0] ?? null); // el value del input es el string con la ruta, la imagen se captura con la propiedad files que es un array
  }

  ngOnInit(): void {

    if (!this.authService.isLogged()) {
      this.router.navigate(["login"]);
    }
    // si nos viene un petId, es porque se nos ha dirigido hasta aqui a traves del boton de editar de fosterhome-card buscamos la informacion de la mascota y rellenamos el formulario
    if (this.petId()) {

      this.petService.getPet(this.petId()!).subscribe({
        next: (data: PetDTO) => {
          this.existentPet.set(data);
          this.petForm.patchValue({
            name: data.name,
            ageGroup: data.ageGroup,
            breed: data.breed,
            species: data.species,
            info: data.info,
            fosterhomeId: data.fosterHomeId.toString()
          });
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("Error de conexion, vuelve a intetarlo cuando tengas conexión");
            window.scroll({behavior: 'smooth', top:0});
            return;
          }
          if (err.status === 403) {
            this.authService.logout("/login");
          } else {
            this.errorMessage.set(err.error.message);
            window.scroll({behavior: 'smooth', top:0});
          }
        }
      });

      // Buscamos tambien las casas de acogida del usuario para montar un select que permita cambiar la mascota de hogar
      this.fosterhomeService.getFosterHomes().subscribe({
        next: (data: FosterHomeDTO[]) => {
          this.userFosterHomes.set(data);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("Error de conexion, vuelve a intetarlo cuando tengas conexión");
            return;
          }
          if (err.status === 403) {
            this.authService.logout("/login");
          } else {
            this.errorMessage.set(err.error.message);
          }
        }
      })
    }

  }

  onReset() {
    this.petForm.reset();
    this.imageClientPath.set(null);
  }

  onSubmit() {

    if (!this.petForm.valid) {
      this.errorMessage.set('El nombre y la especie son obligatorios');
      return;
    }

    // Como puede llevar imagen tenemos que capturar los campos con un formData.
    const formData: FormData = new FormData();
    const values = this.petForm.getRawValue();

    formData.append("name",values.name!); // ! poruqe sabemos que viene, es obligatorio
    formData.append("species",values.species!); // igual que con name
    formData.append("fosterHomeId",values.fosterhomeId ? values.fosterhomeId : this.currentFosterhomeId().toString());
    values.breed && formData.append("breed",values.breed); // && por cortocircuito si lo de la izquierda es falsy ya no ejecuta lo de la derecha
    values.ageGroup && formData.append("ageGroup",values.ageGroup);
    values.info && formData.append("info",values.info);
    this.imageClientPath() && formData.append("image",this.imageClientPath()!); // al ser una funcion TypeScript no puede deducir que la segunda llamada va a tener el mismo resultado que la primera

    // Si no tenemos id, estamos en creacion
    if (!this.petId()) {

      this.petService.createPet(formData).subscribe({
        
        next: () => {this.router.navigate(["fosterhome"])},
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("No tienes conexion a internet, vuelve a intentarlo");
            window.scroll({behavior: 'smooth', top:0});
            return;
          }
          if (err.status === 403) {
            this.authService.logout("/login");
            return 
          }
          this.errorMessage.set(err.error?.message ?? "No se ha podido registar la mascota");
            window.scroll({behavior: 'smooth', top:0});
        }
      });

    // Si tenemos id, estamos en edicion, añadimos el id a la peticion o el servidor nos da error
    } else {
        // los signos de ! porque sabemos que llegado a este punto tenemos id, sino entraria en el if 
        formData.append("id",this.petId()!.toString());

        this.petService.editPet(formData).subscribe({
        
        next: () => {this.router.navigate(["fosterhome"])},
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("No tienes conexion a internet, vuelve a intentarlo");
            window.scroll({behavior: 'smooth', top:0});
            return;
          }
          if (err.status === 403) {
            this.authService.logout("/login");
            return 
          }
          this.errorMessage.set(err.error?.message ?? "No se ha podido registar la mascota");
          window.scroll({behavior: 'smooth', top:0});
        }
      });
    }
  }

  deleteImage() {
    
    // Si tenemos id de mascota hacemos peticion para borrar la imagen y la actualizamos en la vista actual

    if (this.petId()) {
      this.petService.deleteImage(Number(this.petId())).subscribe({
        next: () => {
          this.existentPet.update(pet => ({...pet!, imagePath: undefined }));
        }
      });
    }
  }
}
