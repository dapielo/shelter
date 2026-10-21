export interface AdoptionDTO {
    id: number,
    petId: number,
    petName: string,
    initialDate: string,
    endingDate?: string,
    status: string,
    email?: string,
    species: string,
    imagePath: string
}