import { Component, inject, OnInit, signal } from '@angular/core';
import { AdoptionDTO } from '../dto/AdoptionDTO';
import { AdoptionService } from '../adoption-service';
import { AdoptionGrid } from '../adoption-grid/adoption-grid';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../user/auth-service';

@Component({
  selector: 'app-pending-adoptions',
  imports: [AdoptionGrid],
  templateUrl: './pending-adoptions.html',
  styleUrl: './pending-adoptions.css',
})
export class PendingAdoptions implements OnInit{

  auth = inject(AuthService);
  adoptionService = inject(AdoptionService);

  adoptionsAsOwner = signal<AdoptionDTO[]>([]);
  adoptionsAsAdopter = signal<AdoptionDTO[]>([]);
  errorMessage = signal<string>('');
  message = signal<string>('');
  loadingOwner = signal<boolean>(true);
  loadingAdopter = signal<boolean>(true);

  // cargamos los datos al arrancar el componente
  ngOnInit(): void {
    this.loadData();
  }

  loadData() {

    this.adoptionService.getAdoptionsAsOwner().subscribe({
      next: (data) => {
        this.adoptionsAsOwner.set([...data]);
        this.errorMessage.set('');
        this.loadingOwner.set(false);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 403) {
          this.auth.logout("/login");
        } 
        if (err.status === 0) {
          this.loadingOwner.set(false);
          this.errorMessage.set("Error de conexion, intentalo de nuevo mas tarde.")
        }else {
          this.loadingOwner.set(false);
          this.errorMessage.set(err.error.message);
        }
      }
    });

    this.adoptionService.getAdoptionsAsAdopter().subscribe({
      next: (data) => {
        this.adoptionsAsAdopter.set([...data]);
        this.errorMessage.set('');
        this.loadingAdopter.set(false);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 403) {
          this.auth.logout("/login");
        }
        if (err.status === 0) {
          this.loadingAdopter.set(false);
          this.errorMessage.set("Error de conexion, intentalo de nuevo mas tarde.")
        }else {
          this.loadingAdopter.set(false);
          this.errorMessage.set(err.error.message);
        }
      }
    });
  }

  // estos métodos que son los que hemos asociado en la plantilla a los emits que nos vienen desde las cards, capturan el valor que hemos emitido 
  onAccept(adoptionId: number) {
    this.adoptionService.acceptAdoption(adoptionId).subscribe({
      next: () => {
        this.message.set("Se ha aceptado la peticion de adopción puedes consultar su email del adoptante en el apartado de historial")
        this.errorMessage.set('');
        // refrescamos los datos
        this.loadData();
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 0) {
          this.message.set('');
          this.errorMessage.set('Error de conexion, vuelvelo a intentar más tarde');
        } 
        if (err.status === 403) {
          this.auth.logout("/login");
        } else {
          this.message.set('');
          this.errorMessage.set(err.error.message)
        }
      }
    }); 
  }

  onDecline(adoptionId: number) {
    this.adoptionService.declineAdoption(adoptionId).subscribe({
      next: () => {
        this.message.set("Se ha rechazado la peticion de adopción.")
        this.errorMessage.set('');
        // refrescamos los datos
        this.loadData();
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 0) {
          this.message.set('');
          this.errorMessage.set('Error de conexion, vuelvelo a intentar más tarde');
        } 
        if (err.status === 403) {
          this.auth.logout("/login");

        } else {
          this.message.set('');
          this.errorMessage.set(err.error.message)
        }
      }
    });
    
  }

  onDelete(petId: number) {
    this.adoptionService.deleteAdoption(petId).subscribe({
      next: () => {
        this.message.set("Se ha eliminado la peticion de adopción.")
        this.errorMessage.set('');
        // refrescamos los datos
        this.loadData();
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 0) {
          this.message.set('');
          this.errorMessage.set('Error de conexion, vuelvelo a intentar más tarde');
        } 
        if (err.status === 403) {
          this.auth.logout("/login");

        } else {
          this.message.set('');
          this.errorMessage.set(err.error.message)
        }
      }
    });
    
  }
}
