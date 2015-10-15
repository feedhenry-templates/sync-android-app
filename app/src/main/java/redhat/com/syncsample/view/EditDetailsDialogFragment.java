/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package redhat.com.syncsample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import redhat.com.syncsample.R;
import redhat.com.syncsample.item.ShoppingItem;

/**
 * This is the Dialog users will use to edit Shopping Items
 */
public class EditDetailsDialogFragment extends DialogFragment {

    private static final String SHOPPING_ITEM_KEY = "com.redhat.syncsample.SHOPPING_ITEM";
    private EditDetailsViewHolder holder;
    private ShoppingItem shoppingItem;
    private ListItemsFragment saveHandler;

    public static EditDetailsDialogFragment newInstance(ShoppingItem item) {
        EditDetailsDialogFragment fragment = new EditDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(SHOPPING_ITEM_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_details_dialog, null);
        this.shoppingItem = (ShoppingItem) getArguments().getSerializable(SHOPPING_ITEM_KEY);
        this.holder = new EditDetailsViewHolder(view);
        getDialog().setTitle(R.string.edit_dialog_title);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        holder.bind(shoppingItem, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        holder.unbind();
    }

    private void saveItem() {
        final String newName = holder.nameField.getText().toString();
        final String newCreated = holder.createdField.getText().toString();
        saveHandler.saveItem(shoppingItem, newName, newCreated);
        dismiss();
    }

    public void setSaveHandler(ListItemsFragment saveHandler) {
        this.saveHandler = saveHandler;
    }

    private static class EditDetailsViewHolder {
        final Button saveButton;
        final Button cancelButton;
        final EditText nameField;
        final EditText idField;
        final EditText createdField;


        public EditDetailsViewHolder(View view) {
            this.cancelButton = (Button) view.findViewById(R.id.cancel_button);
            this.saveButton = (Button) view.findViewById(R.id.save_button);
            this.nameField = (EditText) view.findViewById(R.id.item_name_field);
            this.idField = (EditText) view.findViewById(R.id.item_id_field);
            this.createdField = (EditText) view.findViewById(R.id.item_created_field);
        }

        void bind(ShoppingItem item, final EditDetailsDialogFragment fragment) {
            idField.setText(item.getId());
            createdField.setText(item.getCreated());
            nameField.setText(item.getName());
            saveButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  fragment.saveItem();
                                              }
                                          }
            );

            cancelButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    fragment.dismiss();
                                                }
                                            }
            );

        }

        void unbind() {
            this.cancelButton.setOnClickListener(null);
            this.saveButton.setOnClickListener(null);
        }
    }

}
