export interface PetDTO {
    id?: number,
    ownerId: number,
    name: string,
    species: string,
    breed?: string,
    ageGroup?: string,
    info?: string,
    registeredAt?: string,
    fosterHomeId: number
    status?: string,
    imagePath?: string
}