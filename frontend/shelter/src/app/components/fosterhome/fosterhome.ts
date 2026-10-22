import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { FosterHomeService } from '../../services/foster-home-service';
import { FosterHomeDTO } from '../../model/FosterHomeDTO';
import { HttpErrorResponse } from '@angular/common/http';
import { FosterhomeCard } from '../fosterhome-card/fosterhome-card';
import { PetService } from '../../services/pet-service';

@Component({
  selector: 'app-fosterhome',
  imports: [FosterhomeCard],
  templateUrl: './fosterhome.html',
  styleUrl: './fosterhome.css',
})
export class Fosterhome implements OnInit{

  auth = inject(AuthService);
  router = inject(Router);
  fosterHomeService = inject(FosterHomeService);
  petService = inject(PetService);

  fosterHomes = signal<FosterHomeDTO[]>([]);
  errorMessage = signal<string>('');

  // capturamos los eventos con el valor que emiten lso outputs de los botones de las tarjetas
  onEdit (petId: number) {
    this.router.navigate([`pet/edit/:${petId}`]);
  }

  onDeletePet(petId: number) {

    if (confirm("Seguro que quieres borrar?")) {

      this.petService.deletePet(petId).subscribe({
        next: () => {this.router.navigate(["fosterhome"])},
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("Error de conexion, vuelvelo a intentar");
          } else {
            this.errorMessage.set(err.error.message);
          }
        }
      });
    }
  }

  onDeleteFosterHome(fosterHomeId: number) {
    
    if (confirm("Seguro que quieres borrar el hogar? Se perderan las publicaciones de todas sus mascotas")) {

      this.fosterHomeService.deleteFosterHome(fosterHomeId).subscribe({
        next: () => { this.router.navigate(["fosterhome"]) },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorMessage.set("Error de conexion, vuelvelo a intentar");
          } else {
            this.errorMessage.set(err.error.message);
          }
        }
      });
    }
  }

  ngOnInit() {

    this.fosterHomeService.getFosterHomes().subscribe({
      next: data => {
        this.fosterHomes.set([...data]);
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 403) {
          this.auth.logout;
          this.router.navigate(["login"]);
        } else {
          this.errorMessage.set(err.error.message)
        }
      }
    });
  }

}
