package com.david.shelter.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.david.shelter.exception.FileStorageException;
import com.david.shelter.exception.InvalidImageNameException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.util.Validator;


// Este servicio es el que se encarga de subir actualizar y crear las imagenes
@Service 
public class FileService {

    private final UserService userService;
    private final Path uploadsDir;

    // Al instanciar el servicio, creamos la carpeta con la ruta configurada en el properties si esta no existe.
    public FileService(@Value("${upload.dir}") String route, UserService userService) {

        this.uploadsDir = Path.of(route);
        this.userService = userService;

        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new FileStorageException("Error al intentar crear la carpeta de imagenes");
        }
    }

    // CREATE
    // almacena una imagen, dentro de una carpeta asignada a cada usuario a partir de su id
    public String storeImage(MultipartFile file) {        
        
        // Si se sube una imagen, extraemos su extension, le ponemos un nombre unico para uqe no haya conflico
        if (file != null && !file.isEmpty()){
            
            // Comprobamos que tenga un nombre valido
            if (!Validator.isValidImage(file)){
                throw new InvalidImageNameException("El archivo no tiene un nombre válido");
            }

            // Si no existe creamos una carpeta para el usuario dentro de nuestra carpeta uploads
            String userId = userService.getUserFromContext().getId().toString();
            Path userFolder = uploadsDir.resolve(userId);

            try {
                Files.createDirectories(userFolder);
            } catch (IOException e) {
                throw new FileStorageException("No se ha podido crear la carpeta para el usuario");
            }

            // Obtenemos la extension, cambiamos el nombre por uno unico, y creamos la ruta con el nuevo nombre para copiar los datos de la imagen
            String name = file.getOriginalFilename().toLowerCase();
            String extension = name.substring(name.lastIndexOf("."));

            String uniqueName = UUID.randomUUID().toString();
            uniqueName += extension;
            Path imagePath = userFolder.resolve(uniqueName);

            try{
                Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                // Devolvemos la ruta para que se almacene en la informacion de la mascota
                return imagePath.toString();
            } catch (IOException e) {
                throw new FileStorageException("No se ha podido guardar la imagen de la mascota.");
            }

        }
        // Si no se sube imagen, en ese atributo dejamos null;
        return null;
    }

    // READ
    // a Partir de una ruta, busca una imagen y se la devuelve al navegador
    public Resource getImage(String imagePath) {
        
        // Primero comprobamos que la ruta no venga vacía
        if (imagePath == null || imagePath.isBlank()) {
            throw new ResourceNotFoundException("La ruta de la imagen no es correcta");
        }

        Path path = Path.of(imagePath);

        Path absolutePath = path.toAbsolutePath().normalize();
        Path root = this.uploadsDir.toAbsolutePath().normalize();

        // Si la ruta no esta dentro de las rutas permitidas (dentro de nuestra carpeta de uploads o siendo la propia carpeta de uploads) lanzamos error
        if (!absolutePath.startsWith(root) || absolutePath.equals(root)) {
            throw new FileStorageException("Ruta de la imagen no válida");
        }

        try{
            // Ahroa creamos el objeto Resource a partir de la ruta, pero antes de devolver la imagen comprobamos 
            // que exista, aunque la URL este bien escrita, puede no haber una imagen en esa URL
            Resource image = new UrlResource(absolutePath.toUri());

            if (!image.isReadable()) {
                throw new ResourceNotFoundException("No hay ninguna imagen en esa ruta");
            }

            // Si todo esta bien, devolvemos la imagen
            return image;
        } catch (IOException e) {
            throw new FileStorageException("No se ha podido cargar la imagen");
        }

    }

    // UPDATE
    // Para subir una nueva imagen si la queremos reemplazar
    public String updateImage(String imagePath, MultipartFile file) {

        // Si no se sube imagen o está vacía no hacemos nada
        if (file == null || file.isEmpty()) {
            return imagePath;
        }

        String newPath = storeImage(file);
        deleteImage(imagePath);

        return newPath;
    }

    // DELETE
    // Para borrar una imagen cuando se borra su recurso asociado.
    public void deleteImage(String imagePath){

        // Si esta vacio no hacemos nada
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        Path root = this.uploadsDir.toAbsolutePath().normalize();
        Path image = Path.of(imagePath).toAbsolutePath().normalize();

        if (!image.startsWith(root) || image.equals(root)) {
            throw new FileStorageException("Ruta de la imagen no válida");
        }

        // Intentamos borrar (puede lanzar excepcion), este servicio no se encarga de determinar si se puede borrar, eso lo tendra que hacer el
        // servicio que lo llame, presumiblemente PetService
        try {
            Files.deleteIfExists(image);        
        } catch (IOException e) {
            throw new FileStorageException("No se ha podido eliminar la imagen");
        }
    }
}
