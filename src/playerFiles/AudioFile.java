package playerFiles;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AudioFile {
    private String name;
    private String fileType;
    public int getFileDuration() {
        return 0;
    }

    public AudioFile deepCopy() {
        AudioFile copy = new AudioFile();
        copy.name = getName();
        copy.fileType = getFileType();
        return copy;
    }
}
