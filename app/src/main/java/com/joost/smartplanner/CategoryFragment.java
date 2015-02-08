package com.joost.smartplanner;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.joost.category.Category;
import com.joost.database.DatabaseHelper;
import com.joost.layout.CategoryListViewAdapter;
import com.joost.layout.CategorySpinnerAdapter;
import com.joost.layout.ColorAdapter;
import com.joost.layout.SpinnerItemColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * DONE: test rebuildTree (lft & rgt)
 */
public class CategoryFragment extends Fragment {

    List<Category> allCategories = new ArrayList<Category>();
    DatabaseHelper dbH;
    Button btCreateCategory;
    Button btRebuildTree;
    TextView tvTree;
    ListView categoryListView;
    CategoryListViewAdapter categoryAdapter;
    LinearLayout fragmentContainer;


    ArrayList<SpinnerItemColor> colors = new ArrayList<SpinnerItemColor>();


    public CategoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbH = ((MainFragmentActivity) getActivity()).getDatabaseHelper();
        getAllCategories();

        Resources res = getActivity().getResources();
        initiateColors(colors, res);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        btCreateCategory = (Button) root.findViewById(R.id.bt_add_category);
        btRebuildTree = (Button) root.findViewById(R.id.bt_rebuild_tree);
        tvTree = (TextView) root.findViewById(R.id.tv_category_tree);
        categoryListView = (ListView)root.findViewById(R.id.categoryListView);
        fragmentContainer = (LinearLayout)root.findViewById(R.id.fragment_container);


        categoryAdapter = new CategoryListViewAdapter(getActivity(), android.R.layout.simple_list_item_1, allCategories);
        categoryListView.setAdapter(categoryAdapter);
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: do something with OnItemClickListener
            }
        });
        categoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                //TODO: When category is deleted also delete it from all the events (set it to -1 for example)
                //Update categories to get right thing
                getAllCategories();
                final Category clickedCategory = allCategories.get(index);

                //Build confirmation dialog
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle("Delete Category");
                alertBuilder.setMessage("Not only the category but also all the sub-categories will be deleted. Do you want to delete \""+clickedCategory.getName()+"\"?");
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });
                alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Delete category and everything else
                        dbH.deleteCategory(clickedCategory);
                        //Update correct to refresh the list
                        getAllCategories();
                        dbH.rebuildCategoryTree(allCategories.get(0),1);
                        //Update once more to get the right values for all categories
                        getAllCategories();
                        //Update ListView
                        categoryAdapter.swapData(allCategories);
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();

                return false;
            }
        });

        displayCategories();

        //OnClickListeners
        btCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateCategoryDialog();
            }
        });

        btRebuildTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category parent = allCategories.get(0);
                dbH.rebuildCategoryTree(parent, 1);
                getAllCategories();
                displayCategories();
                //Update ListView
                categoryAdapter.swapData(allCategories);
            }
        });

        return root;
    }

    private void showCreateCategoryDialog() {
        //Dialog to add new category
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.add_category_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        //set the view to the dialog
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Create new Category");
        //store editText and spinner
        final EditText categoryName = (EditText) promptsView.findViewById(R.id.categoryName);
        final Spinner parentSpinner = (Spinner) promptsView.findViewById(R.id.parentSpinner);
        final Spinner colorSpinner = (Spinner) promptsView.findViewById(R.id.colorSpinner);


        getAllCategories();

        //fill spinner with up-to-date categories
        CategorySpinnerAdapter categorySpinnerAdapter = new CategorySpinnerAdapter(getActivity(), R.layout.custom_spinner_item, allCategories);
        parentSpinner.setAdapter(categorySpinnerAdapter);
        //Fill color spinner
        ColorAdapter colorAdapter = new ColorAdapter(getActivity(), android.R.layout.simple_spinner_item, colors);
        colorSpinner.setAdapter(colorAdapter);

        //Cancel action
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        //Create action
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName;
                long newParentId;
                int newColor;

                //Get the name
                if (categoryName.getText().toString() == "") {
                    Log.d("Category Name", "Name is empty");
                    Random r = new Random();
                    newName = "Random Category " + r.nextInt(99);
                } else {
                    newName = categoryName.getText().toString();
                }

                //get the parent
                newParentId = allCategories.get(parentSpinner.getSelectedItemPosition()).getId();

                //Get the color
                newColor = colors.get(colorSpinner.getSelectedItemPosition()).getColor();

                //Create new Category
                Category newCategory = new Category(0, newName, newParentId, 0, 0, newColor);
                dbH.createCategory(newCategory);

                //Recreate tree
                getAllCategories();
                displayCategories();


                categoryAdapter.swapData(allCategories);

            }
        });

        //Create dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void initiateColors(ArrayList<SpinnerItemColor> colors, Resources res) {

        //Color spinner
        colors.add(new SpinnerItemColor("Red", res.getColor(R.color.material_red), true));
        colors.add(new SpinnerItemColor("Pink", res.getColor(R.color.material_pink), true));
        colors.add(new SpinnerItemColor("Purple", res.getColor(R.color.material_purple), true));
        colors.add(new SpinnerItemColor("Deep Purple", res.getColor(R.color.material_deep_purple), true));
        colors.add(new SpinnerItemColor("Indigo", res.getColor(R.color.material_indigo), true));
        colors.add(new SpinnerItemColor("Blue", res.getColor(R.color.material_blue), true));
        colors.add(new SpinnerItemColor("Light Blue", res.getColor(R.color.material_light_blue), true));
        colors.add(new SpinnerItemColor("Cyan", res.getColor(R.color.material_cyan), true));
        colors.add(new SpinnerItemColor("Teal", res.getColor(R.color.material_teal), true));
        colors.add(new SpinnerItemColor("Green", res.getColor(R.color.material_green), true));
        colors.add(new SpinnerItemColor("Light Green", res.getColor(R.color.material_light_green), true));
        colors.add(new SpinnerItemColor("Lime", res.getColor(R.color.material_lime), true));
        colors.add(new SpinnerItemColor("Yellow", res.getColor(R.color.material_yellow), true));
        colors.add(new SpinnerItemColor("Amber", res.getColor(R.color.material_amber), true));
        colors.add(new SpinnerItemColor("Orange", res.getColor(R.color.material_orange), true));
        colors.add(new SpinnerItemColor("Deep Orange", res.getColor(R.color.material_deep_orange), true));
        colors.add(new SpinnerItemColor("Brown", res.getColor(R.color.material_brown), true));
        colors.add(new SpinnerItemColor("Grey", res.getColor(R.color.material_grey), true));
        colors.add(new SpinnerItemColor("Blue Grey", res.getColor(R.color.material_blue_grey), true));
    }

    private void getAllCategories() {
        allCategories = dbH.getAllCategories();
    }

    private void displayCategories() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < allCategories.size(); i++) {
            for (int j = 0; j < allCategories.get(i).getLayer(); j++) {
                stringBuilder.append("      ");
            }
            stringBuilder.append(allCategories.get(i).getName());
            stringBuilder.append("(lft: " + allCategories.get(i).getLft() + " ,rgt: " + allCategories.get(i).getRgt() + " ,layer: " + allCategories.get(i).getLayer() + ")");
            //stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append("\n");
        }
        tvTree.setText(stringBuilder.toString());
    }

}
