package org.example.helpers;

import org.example.exceptions.FolderAlreadyExistsException;
import org.example.exceptions.FolderNotFoundException;
import org.example.exceptions.InvalidPathException;
import org.example.parts.Mail;

import java.util.*;

public class Directory {
    private String rootFolderName;
    private final HashMap<String, Set<Mail>> emails;
    private final HashMap<String, Set<String>> folders;

    public Directory(String rootFolderName) {
        this.rootFolderName = rootFolderName;
        this.emails = new HashMap<>();
        this.folders = new HashMap<>();
        this.folders.put(rootFolderName, new HashSet<>());
        this.emails.put(rootFolderName, new HashSet<>());
    }


    //Create new folder in any existing folder
    public void addChildrenFolderToRoot(String newChildFolder, String rootFolder) {
        //Get the children folders of the folder where we want to put the new folder
        Set<String> existingChildFolders = this.folders.get(rootFolder);

        existingChildFolders.add(newChildFolder);
        this.folders.put(rootFolder, existingChildFolders);
        this.folders.put(newChildFolder, new HashSet<>());
    }

    public boolean checkIfPathIsValid(List<String> fullPath) {
        for (int i = 1; i < fullPath.size(); i++) {
            String rootDir = fullPath.get(i - 1);
            String childDir = fullPath.get(i);

            //Check if there is such a folder in the directory
            if (!this.folders.get(rootDir).contains(childDir)) {
                throw new InvalidPathException("Invalid path!");
            }
        }

        return true;
    }

    public void addChildrenFolderToDirectory(String newFolder, List<String> fullPath) {
        //Check if the path starts from the root folder
        if (fullPath.get(0).equals(rootFolderName)) {
            int size = fullPath.size();
            checkIfPathIsValid(fullPath);
            //Check if already such a folder exists
            if (this.folders.get(fullPath.get(size - 1)).contains(newFolder)) {
                throw new FolderAlreadyExistsException("Folder with the same name already exists!");
            }
            this.addChildrenFolderToRoot(newFolder, fullPath.get(size - 1));
            this.emails.put(newFolder, new HashSet<>());
        } else {
            throw new InvalidPathException("The path must start from the root folder!");
        }
    }

    public void removeFolderAndFolderChilds(String folderToRemove, List<String> fullPath) {
        while (this.folders.containsKey(folderToRemove)) {
            removeFolderFromDirectory(folderToRemove, fullPath);
        }

    }

    public void removeFolderFromDirectory(String folderToRemove, List<String> fullPath) {
        checkIfPathIsValid(fullPath);
        String rootFolder = fullPath.get(fullPath.size() - 1);
        if (!folders.get(rootFolder).contains(folderToRemove)) {
            throw new IllegalArgumentException("There is not such a folder in this directory!");
        }
        //Delete the emails from the folder
        this.emails.remove(folderToRemove);
        //Get the children folders of the folder
        List<String> childFolders = new ArrayList<>(folders.get(folderToRemove));
        //If the folder has no children, delete the folder
        if (childFolders.isEmpty()) {
            Set<String> rootFolderChilds = folders.get(rootFolder);
            //remove folderToRemove from its root directory
            rootFolderChilds.remove(folderToRemove);
            folders.put(rootFolder, rootFolderChilds);
            folders.remove(folderToRemove);
        }

        //If the folder has children delete them and all the folders bellow
        else {
            String childFolder = childFolders.get(0);
            childFolders.remove(0);
            List<String> newPath = new ArrayList<>(fullPath);
            newPath.add(folderToRemove);
            removeFolderFromDirectory(childFolder, newPath);
        }
    }

    public void addEmail(String folderName, Mail email) {
        if (!folders.containsKey(folderName)) {
            throw new FolderNotFoundException("Such a folder does not exist!");
        }

        this.emails.putIfAbsent(folderName, new HashSet<>());
        Set<Mail> emails = this.emails.get(folderName);
        emails.add(email);
        this.emails.put(folderName, emails);


    }

    public void removeEmail(String rootFolderName, Mail email) {
        if (!folders.containsKey(rootFolderName)) {
            throw new FolderNotFoundException("Such a folder does not exist!");
        }

        Set<Mail> emails = this.emails.get(rootFolderName);
        if (!emails.remove(email)) {
            throw new IllegalArgumentException("Such an email does not exist in this folder!");
        }
        this.emails.put(rootFolderName, emails);

    }

    public HashMap<String, Set<Mail>> getEmails() {
        return emails;
    }

    public HashMap<String, Set<String>> getFolders() {
        return folders;
    }

    public String getRootFolderName() {
        return rootFolderName;
    }

    public void setRootFolderName(String rootFolderName) {
        this.rootFolderName = rootFolderName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory directory = (Directory) o;
        return Objects.equals(rootFolderName, directory.rootFolderName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootFolderName);
    }

    @Override
    public String toString() {
        return "Directory{" +
                "rootFolderName='" + rootFolderName + '\'' +
                ", emails=" + emails +
                ", folders=" + folders +
                '}';
    }
}
