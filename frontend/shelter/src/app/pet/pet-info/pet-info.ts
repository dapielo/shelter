import { Component, computed, effect, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { PetService } from '../../pet/pet-service';
import { PetDTO } from '../dto/PetDTO';
import { SpeciesLabel } from '../dto/SpeciesLabels';
import { AgeGroupLabel } from '../../adoption/dto/AgeGroupLabel';
import { environment } from '../../../environments/environment';
import { AdoptionService } from '../../adoption/adoption-service';
import { AuthService } from '../../user/auth-service';
import { LngLat, LngLatBounds, Map as MapLibre, setWorkerUrl } from 'maplibre-gl';

// Como usamos vite, nos dice en la documentacion que tenemos que indicar donde esta el worker https://maplibre.org/maplibre-gl-js/docs/#installation
setWorkerUrl('/maplibre-gl-worker.mjs');

@Component({
    selector: 'app-pet-info',
    imports: [],
    templateUrl: './pet-info.html',
    styleUrl: './pet-info.css',
})
export class PetInfo implements OnInit, OnDestroy {

    // Inyeccion de dependencias
    auth = inject(AuthService);
    router = inject(Router);
    petService = inject(PetService);
    adoptionService = inject(AdoptionService);

    // input es el que se usa para recibir parametros desde el padre, pero en este caso recibimos el :id de la ruta (tienen que coincidir en nombre)
    // recordar que es un signal, se llama como funncion ()
    id = input.required<string>();
    pet = signal<PetDTO | null>(null);
    petFormatted = computed(() => {
        return {
            ...this.pet(),
            species: SpeciesLabel[this.pet()?.species ?? ''] ?? 'desconocido',
            ageGroup: AgeGroupLabel[this.pet()?.ageGroup ?? ''] ?? 'desconocido'
        }
    })
    errorMessage = signal<string>('');
    imageRoute = computed<string>(() => {
        return this.petFormatted().imagePath ? `${environment.api}/image/${this.petFormatted().imagePath}` : '/images/default_photo.png'
    });
    isOwner = computed(() => this.pet()?.ownerId === this.auth.loadUser()?.id);

    // El mapa de maplibre, lo inicializamos a null, y en el onInit cargamos los datos junto a la mascota si esta los trae
    map: MapLibre | null = null;

    ngOnInit() {

        this.petService.getPet(Number(this.id())).subscribe({
            next: (data) => {
                this.pet.set(data);
            },
            error: (err) => {
                this.errorMessage.set(err.error.message);
            }
        });
    }

    constructor() {

        effect(() => {
            const pet = this.pet();

            if (pet?.coordinates) {

                const southWest = new LngLat(this.pet()!.coordinates[2], this.pet()!.coordinates[0]);
                const northEast = new LngLat(this.pet()!.coordinates[3], this.pet()!.coordinates[1]);

                this.map = new MapLibre({
                    container: 'map',
                    style: 'https://tiles.openfreemap.org/styles/liberty',
                    bounds: new LngLatBounds(southWest, northEast)
                });
            }
        })
    }

    initiateAdoption() {

        if (!this.auth.isLogged()) {
            this.router.navigate(["login"], { queryParams: { error: "Para poder iniciar una adopcion tienes que iniciar sesion" } });
            return;
        }

        this.adoptionService.iniciateAdoption(Number(this.id())).subscribe({
            next: (data) => {
                alert("La adopcion se ha iniciado, ahora tienes que esperar a recibir una respuesta por parte del hogar de acogida. Se te va a redirigir a tu listado de adociones.")
                this.router.navigate(["/adoption", "pending"]);
            },
            error: (err) => {
                this.errorMessage.set("No se ha podido iniciar la adopcion: " + err.error.message);
            }
        });
    }

    // Lo eliminamos al destruir el componente
    ngOnDestroy() {
        this.map?.remove();
    }
}
