package com.gojiyajayesh.chatvista.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {
    public static String currentUserId()
    {
        return   FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetail(){
        return FirebaseFirestore.getInstance().collection("Users").document(currentUserId());
    }
    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("Users");
    }
    public static String getIndividualChatId(String SenderId,String ReceiverId)
    {
        if(SenderId.hashCode()<ReceiverId.hashCode())
        {
            return SenderId+"_"+ReceiverId;
        }
        else
        {
            return ReceiverId+"_"+SenderId;
        }
    }
    public static DocumentReference getAllChatsCollection(String IndividualChatId)
    {
        return FirebaseFirestore.getInstance().collection("Chats").document(IndividualChatId);
    }
    public static CollectionReference getChatRoomMessageREferense(String ChatroomId)
    {
        return getAllChatsCollection(ChatroomId).collection("Chats");
    }
}
