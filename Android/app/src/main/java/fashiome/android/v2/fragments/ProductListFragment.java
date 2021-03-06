package fashiome.android.v2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fashiome.android.R;
import fashiome.android.adapters.ProductAdapter;
import fashiome.android.models.Product;
import fashiome.android.utils.Constants;
import fashiome.android.v2.adapters.ProductReviewRecyclerViewAdapter;

public class ProductListFragment extends Fragment {

    @Bind(R.id.rcvProductRecyclerView)
    RecyclerView productRecyclerView;

    KProgressHUD hud = null;
    int mId = -1;

    public ProductListFragment() {}
    //public ProductListFragment(int id) {super(); mId = id;}

    public static ProductListFragment newInstance() {
        ProductListFragment frag = new ProductListFragment();
        return frag;
    }

    public void setProductParseQuery(ParseQuery<Product> productParseQuery) {
        this.productParseQuery = productParseQuery;
    }

    private ParseQuery<Product> productParseQuery;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = (View) inflater.inflate(R.layout.fragment_product_list_v2, container, false);

        ButterKnife.bind(this, view);

        if (productAdapter == null) {
            productAdapter = new ProductAdapter(getActivity());
        }

        if (productRecyclerView.getAdapter() == null) {
            productRecyclerView.setAdapter(productAdapter);
        }

        if (productRecyclerView.getLayoutManager() == null ) {
            productRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        if (productParseQuery != null) {
            productParseQuery.findInBackground(new FindCallback<Product>() {
                @Override
                public void done(List<Product> products, ParseException e) {
                    newData(products);
                }
            });
        }
        return view;
    }

    public void truncateData(){
        if(productAdapter != null) {
            productAdapter.removeAll();
            productAdapter.notifyDataSetChanged();
        }
    }

    public void newData(List<Product> products){
        if(productAdapter != null) {
            productAdapter.updateItems(Constants.NEW_SEARCH_OPERATION, products);
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
