package code;

import javax.sound.midi.*;
import java.io.File;

public class MidiBackEnd {


        public static void appendMidiWithPitchVelocity(String inputFilePath, String outputFilePath, int pitch, int velocity, int row, boolean radioVel) {

            try {
                Sequence inputSequence = MidiSystem.getSequence(new File(inputFilePath));
                Sequence outputSequence;

                File outputFile = new File(outputFilePath);
                if (row!=0) {
                    outputSequence = MidiSystem.getSequence(new java.io.File(outputFilePath));
                } else {
                    outputSequence = MidiSystem.getSequence(new java.io.File(inputFilePath));
                }
                Track  newTrack = outputSequence.getTracks()[0];

                for (Track track : inputSequence.getTracks()) {


                    for (int i = 0; i < track.size(); i++) {
                        MidiEvent event = track.get(i);
                        MidiMessage message = event.getMessage();

                        if (message instanceof ShortMessage) {
                            ShortMessage shortMessage = (ShortMessage) message;
                            int command = shortMessage.getCommand();
                            int channel = shortMessage.getChannel();
                            int data1 = shortMessage.getData1();
                            int data2 = shortMessage.getData2();
                            int modifiedData2;
                            if (command == ShortMessage.NOTE_ON || command == ShortMessage.NOTE_OFF) {
                                int modifiedData1 = data1 + pitch;
                                modifiedData1 = Math.min(127, Math.max(0, modifiedData1));
                                if(radioVel){
                                     modifiedData2 = data2+velocity;
                                }
                                else modifiedData2 = velocity;

                                ShortMessage newShortMessage = new ShortMessage();
                                newShortMessage.setMessage(command, channel, modifiedData1, modifiedData2);
                                MidiEvent newEvent = new MidiEvent(newShortMessage, event.getTick());
                                newTrack.add(newEvent);
                            }
                        }

                        // Always add the original event to the new track
                        if(row==0){
                        MidiEvent originalEvent = new MidiEvent(message, event.getTick());
                        newTrack.add(originalEvent);}
                    }
                }

                MidiSystem.write(outputSequence, 1, outputFile);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }




}