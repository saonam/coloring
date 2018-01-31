package de.frype.coloring.book_selection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import de.frype.coloring.library.Library;
import de.frype.coloring.R;

/**
 * Created by Jan on 22.11.2015.
 */
public class BookSelectionAdapter extends BaseAdapter {

    private final Context context;
    private final int size;
    private final Library library = Library.getInstance();

    public BookSelectionAdapter(Context context) {
        this.context = context;
        this.size = context.getResources().getDimensionPixelSize(R.dimen.book_preview_width);
    }

    @Override
    public int getCount() {
        return library.getNumberBooks();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if (convertView == null) {
            view = View.inflate(context, R.layout.element_book_selection, null);
            view.setLayoutParams(new GridView.LayoutParams(size, size));
        } else {
            view = convertView;
        }

        library.setCurrentBook(position);
        TextView categoryNameView = view.findViewById(R.id.bookNameTextView);
        categoryNameView.setText(library.getStringFromCurrentBook("name"));
        categoryNameView.setText("test");

        return view;

    }
}
