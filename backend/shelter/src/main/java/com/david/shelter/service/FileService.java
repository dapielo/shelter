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
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import com.david.shelter.exception.FileStorageException;
import com.david.shelter.exception.InvalidImageNameException;
import com.david.shelter.exception.ResourceNotFoundException;
import com.david.shelter.model.entity.User;
import com.david.shelter.util.Validator;

import lombok.extern.slf4j.Slf4j;


// Este servicio es el que se encarga de subir actualizar y crear las imagenes
@Slf4j // para usar un logger de terminar
@Service 
public class FileService {

    private final Path uploadsDir;

    // Al instanciar el servicio, creamos la carpeta con la ruta configurada en el properties si esta no existe.
    public FileService(@Value("${upload.dir}") String route) {

        this.uploadsDir = Path.of(route);

        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new FileStorageException("Error al intentar crear la carpeta de imagenes");
        }
    }

    // CREATE
    // almacena una imagen, dentro de una carpeta asignada a cada usuario a partir de su id, el usuario va por parametro porque si metemos UserService como dependencia
    // nos da un error de dependencia circular: FileService -> UserService -> FileService ...
    public String storeImage(MultipartFile file, User user) {        
        
        // Si se sube una imagen, extraemos su extension, le ponemos un nombre unico para uqe no haya conflico
        if (file != null && !file.isEmpty()){
            
            // Comprobamos que tenga un nombre valido
            if (!Validator.isValidImage(file)){
                throw new InvalidImageNameException("El archivo no tiene un nombre válido");
            }

            // Si no existe creamos una carpeta para el usuario dentro de nuestra carpeta uploads
            String userId = user.getId().toString();
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
                // Devolvemos la ruta para que se almacene en la informacion de la mascota, OJO, devolvemos solo la parte de la ruta a partir de la carpeta uploads/ (no incluida)
                return userId + "/" + uniqueName;
            } catch (IOException e) {
                throw new FileStorageException("No se ha podido guardar la imagen de la mascota.");
            }

        }
        // Si no se sube imagen, en ese atributo dejamos null;
        return null;
    }

    // READ
    // a Partir de una ruta, busca una imagen y se la devuelve al navegador
    public Resource getImage(String imageRoute) {
        
        // Primero comprobamos que la ruta no venga vacía
        if (imageRoute == null || imageRoute.isBlank()) {
            throw new ResourceNotFoundException("La ruta de la imagen no es correcta");
        }

        // Ahora construimos la ruta de la imagen, recordar que la ruta que almacena la entidad Pet es solo a partir de la ruta de la carpeta uploads
        Path path = uploadsDir.resolve(imageRoute);

        Path absolutePath = path.toAbsolutePath().normalize();
        Path root = this.uploadsDir.toAbsolutePath().normalize();

        // Si la ruta no esta dentro de las rutas permitidas (dentro de nuestra carpeta de uploads o siendo la propia carpeta de uploads) lanzamos error
        // esto es por si en lugar de la ruta almacenada en la mascota, en la peticion nos inyectan una ruta que no esta permitida o intentan escalar en el sistema de archivos con "../"
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
    public String updateImage(String imagePath, MultipartFile file, User user) {

        // Si no se sube imagen o está vacía no hacemos nada
        if (file == null || file.isEmpty()) {
            return imagePath;
        }

        String newPath = storeImage(file, user);
        deleteImage(imagePath);

        return newPath;
    }

    // DELETE
    // Para borrar una imagen cuando se borra su recurso asociado.
    public void deleteImage(String imageRoute){

        // Si esta vacio no hacemos nada
        if (imageRoute == null || imageRoute.isBlank()) {
            return;
        }

        Path root = this.uploadsDir.toAbsolutePath().normalize();
        Path imagePath = uploadsDir.resolve(imageRoute).toAbsolutePath().normalize();

        if (!imagePath.startsWith(root) || imagePath.equals(root)) {
            throw new FileStorageException("Ruta de la imagen no válida");
        }

        // Intentamos borrar (puede lanzar excepcion), este servicio no se encarga de determinar si se puede borrar, eso lo tendra que hacer el
        // servicio que lo llame, presumiblemente PetService
        try {
            Files.deleteIfExists(imagePath);        
        } catch (IOException e) {
            throw new FileStorageException("No se ha podido eliminar la imagen");
        }
    }

    // Para borrar la carpeta asociada a un usuario cuando este borra su cuenta
    public void deleteUserFolder(Long id) {
        Path folderPath = uploadsDir.resolve(id.toString());

        try {
            // Esta utilidad de Spring permite borrar una carpeta con su contenido, Files.delete solo borra archivos o carpetas vacias
            FileSystemUtils.deleteRecursively(folderPath);
        } catch (IOException e) {
           log.warn("No se ha podido borrar la carpeta de imagenes del usuario");
        }
    }
}
