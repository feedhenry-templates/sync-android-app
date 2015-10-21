/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package com.feedhenry.sync.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import com.feedhenry.sync.R;
import com.feedhenry.sync.item.ShoppingItem;

/**
 * This is the Dialog which users will use to create Shopping Items.
 */
public class CreateItemDialogFragment extends DialogFragment {

    private EditDetailsViewHolder holder;
    private ListItemsFragment createHandler;

    public static CreateItemDialogFragment newInstance() {
        CreateItemDialogFragment fragment = new CreateItemDialogFragment();
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
        View view = View.inflate(container.getContext(), R.layout.save_details_dialog, null);
        this.holder = new EditDetailsViewHolder(view);
        getDialog().setTitle(R.string.create_item_dialog_title);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        holder.bind(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        holder.unbind();
    }

    private void saveItem() {
        final String newName = holder.nameField.getText().toString();
        final String newCreated = String.valueOf(new Date().getTime());
        createHandler.createItem(newName, newCreated);
        dismiss();
    }

    public void setCreateHandler(ListItemsFragment createHandler) {
        this.createHandler = createHandler;
    }

    private static class EditDetailsViewHolder {
        final Button saveButton;
        final Button cancelButton;
        final EditText nameField;



        public EditDetailsViewHolder(View view) {
            this.cancelButton = (Button) view.findViewById(R.id.cancel_button);
            this.saveButton = (Button) view.findViewById(R.id.save_button);
            this.nameField = (EditText) view.findViewById(R.id.item_name_field);

        }

        void bind(final CreateItemDialogFragment fragment) {
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
