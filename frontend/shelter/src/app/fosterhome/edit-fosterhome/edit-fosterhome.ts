import { Component, inject, input, OnInit, signal } from '@angular/core';
import { ProvinceLabel } from '../dto/ProvinceLabel';
import { FormControl, FormGroup, Validators, ɵInternalFormsSharedModule, ReactiveFormsModule } from '@angular/forms';
import { FosterHomeService } from '../foster-home-service';
import { FosterHomeDTO } from '../dto/FosterHomeDTO';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../user/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-fosterhome',
  imports: [ReactiveFormsModule],
  templateUrl: './edit-fosterhome.html',
  styleUrl: './edit-fosterhome.css',
})
export class EditFosterhome implements OnInit{

  provinceSelect = Object.entries(ProvinceLabel).map(([key, value]) => ({key: key, value: value}));

  fosterHomeService: FosterHomeService = inject(FosterHomeService);
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);

  // Lo capturo de la ruta
  fosterhomeId = input.required();
  currentFosterHome = signal<FosterHomeDTO | null>(null);
  errorMessage = signal<string>('');

  fosterhomeForm = new FormGroup({

    name: new FormControl('',Validators.required),
    province: new FormControl('',Validators.required)
  });

  ngOnInit(): void {
    
    this.fosterHomeService.getFosterHomes().subscribe({
      next: (data: FosterHomeDTO[]) => {
        this.currentFosterHome.set(data.find(fh => fh.id === Number(this.fosterhomeId())) ?? null);
        this.fosterhomeForm.patchValue({
          name: this.currentFosterHome()?.name,
          province: this.currentFosterHome()?.province
        });
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set('No tienes conexion a internet, intentalo de nuevo mas tarde');
          window.scroll({top: 0, behavior: 'smooth'});
          return;
        }
        if (err.status === 403) {
          this.authService.logout("/login");
          return;
        } else {
          this.errorMessage.set(err.error?.message ?? 'No se han podido cargar los datos');
          window.scroll({top: 0, behavior: 'smooth'});
          return;
        }
      }
    });
  }

  onSubmit() {

    if (this.fosterhomeForm.invalid) {
      this.errorMessage.set("Ambos campos son oligatorios");
      window.scroll({top: 0, behavior: 'smooth'});
      return;
    }

    const body: FosterHomeDTO = {
      id: Number(this.fosterhomeId()),
      province: this.fosterhomeForm.controls.province.value!,
      name: this.fosterhomeForm.controls.name.value!
    }

    //... seguir, construir peticion de actualizar el this.fosterhomeForm, reedirigir a el listado y editar estilos

    const updatedFosterHome : FosterHomeDTO = {
      id: Number(this.fosterhomeId()),
      name: this.fosterhomeForm.controls.name.value!,
      province: this.fosterhomeForm.controls.province.value!
    }

    this.fosterHomeService.editFosterHome(updatedFosterHome).subscribe({
      next: () => {
        this.router.navigate(["/fosterhome"]);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set("No tienes conexion a internet, intentalo de nuevo más tarde.");
        }
        if (err.status === 403) {
          this.authService.logout("/login");
          return;
        } else {
          this.errorMessage.set(err.error.message ?? 'No se ha podido completar la solicitud');
        }
      }
    })

  }
}
