package com.goldadorn.main.activities.productListing;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.goldadorn.main.BR;
import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.activities.BaseActivity;
import com.goldadorn.main.bindings.BindingRecycleItemHolder;
import com.goldadorn.main.databinding.ProductPickGridItemBinding;
import com.goldadorn.main.model.Designer;
import com.goldadorn.main.model.FilterCollection;
import com.goldadorn.main.model.FilterPrice;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.FilterType;
import com.goldadorn.main.modules.modulesCore.CodeDataParser;
import com.goldadorn.main.modules.modulesCore.DefaultProjectDataManager;
import com.goldadorn.main.utils.ResultFormating;
import com.goldadorn.main.utils.URLHelper;
import com.google.gson.Gson;
import com.kimeeo.library.actions.LoadDataAQuery;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.PageData;
import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;
import com.kimeeo.library.listDataView.recyclerView.verticalViews.ResponsiveView;

import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class ProductsFragment extends ResponsiveView implements DefaultProjectDataManager.IDataManagerDelegate {
    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DividerDecoration(this.getActivity());
    }

    private HashSet<FilterProductListing> previouslySelected = new HashSet<>();

    public void onItemClick(Object baseObject) {
        FilterProductListing file = (FilterProductListing) baseObject;
        if (ServerProducts.isCallerPTB) {
            boolean status = previouslySelected.add(file);
            if (!status)
                previouslySelected.remove(file);
            if (canProceed(file)) {
                //// TODO: 13-07-2016  for selection made ui change;
                EventBus.getDefault().post(file);
            }
        } else EventBus.getDefault().post(file);

    }


    private boolean canCheckSelected = false;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        if (ServerProducts.isCallerPTB) {
            if (canCheckSelected) {
                View singleItemView = getRecyclerView().getLayoutManager().getChildAt(position);//dont use the parent, its null always
                View ivSelectedSymbol = singleItemView.findViewById(R.id.ivSelectedSymbol);
                ivSelectedSymbol.setVisibility(ivSelectedSymbol.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }


    protected Application getApp() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        return baseActivity.getApp();
    }

    private boolean canProceed(FilterProductListing file) {
        if (previouslySelected.size() == 4) {
            canCheckSelected = false;
            previouslySelected.remove(file);
            Toast.makeText(getContext(), "You can only select a maximum of three products", Toast.LENGTH_SHORT).show();
            return false;
        }
        canCheckSelected = true;
        return true;
    }

    public void onCallEnd(List<?> dataList, boolean isRefreshData) {
        super.onCallEnd(dataList, isRefreshData);
        loadLikes(dataList);
    }


    private void getScissoredListBasedOnIsEnabledFlag() {
        //// TODO: 22-06-2016
    }

    private void loadLikes(final List<?> dataList) {
        if (dataList != null && dataList.size() != 0) {
            String list = "";
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i) instanceof FilterProductListing) {
                    FilterProductListing item = (FilterProductListing) dataList.get(i);
                    list += item.getProdId() + "";
                    if (i != (dataList.size() - 1)) {
                        list += ",";
                    }
                }
            }


            try {
                LoadDataAQuery data = new LoadDataAQuery(getActivity());
                data.setCookies(getApp().getCookies());
                LoadDataAQuery.Result result = new LoadDataAQuery.Result() {
                    @Override
                    public void done(String url, Object json, Object status) {
                        String resp = (String) json;
                        Log.d("djfilter", "filter response: " + resp);
                        if (json != null && json instanceof String) {
                            json = ResultFormating.formating((String) json);
                            Gson gson = new Gson();
                            ProductLikeData data = gson.fromJson((String) json, ProductLikeData.class);
                            if (data.data != null && data.data.size() != 0) {
                                FilterProductListing filterProductListing;
                                for (ProductLikeData.ProductLike productLike : data.data) {
                                    for (int i = 0; i < dataList.size(); i++) {
                                        if (dataList.get(i) != null && dataList.get(i) instanceof FilterProductListing) {
                                            filterProductListing = (FilterProductListing) dataList.get(i);
                                            if (filterProductListing.getProdId() == productLike.getProductId()) {
                                                filterProductListing.setLikeCount(productLike.getLikeCount() + "");
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                };


                Map<String, Object> params = new HashMap<>();

                list = "{\"prodIds\":[" + list + "]}";
                params.put(AQuery.POST_ENTITY, new StringEntity(list));
                String url = URLHelper.getInstance().getProductsLikes();
                data.perform(url, result, params, "");

            } catch (Exception e) {
            }
        }
    }

    ArrayList<Parcelable> filters;
    String sort;

    public void setFilters(ArrayList<Parcelable> filters, String sort) {
        this.filters = filters;
        this.sort = sort;
    }

    public class ProductLikeData {
        public List<ProductLike> data;

        public class ProductLike {
            private int productId;
            private int likeCount;

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public int getLikeCount() {
                return likeCount;
            }

            public void setLikeCount(int likeCount) {
                this.likeCount = likeCount;
            }
        }
    }


    protected DataManager createDataManager() {
        return new DataManager(getActivity(), this, getApp().getCookies());
    }

    public Map<String, Object> getNextDataParams(PageData data) {
        Map<String, Object> params = new HashMap<>();
        String param = getParam(offset);

        try {
            params.put(AQuery.POST_ENTITY, new StringEntity(param));
        } catch (Exception e) {
        }

        return params;
    }

    private String getParam(int offset) {
        if (offset != -1) {
            Log.d("dj", "getParams - ProductsFrag ---offset != -1");
            if (filters == null || filters.size() == 0) {
                Log.d("dj", "getParams - ProductsFrag--filters null or size == 0");
                String val = "{\"offset\" :" + offset;
                val += ",\"sort\" : \"" + sort + "\"}";
                Log.d("dj", "getParams - val: " + val);
                return val;
            } else {
                Log.d("dj", "getParams - ProductsFrag ---else filter not null");
                List<Designer> designerList = new ArrayList<>();
                List<FilterCollection> collectionList = new ArrayList<>();
                List<FilterPrice> priceList = new ArrayList<>();
                List<FilterType> typeList = new ArrayList<>();
                for (Parcelable filter : filters) {
                    if (filter instanceof Designer)
                        designerList.add((Designer) filter);
                    else if (filter instanceof FilterCollection)
                        collectionList.add((FilterCollection) filter);
                    else if (filter instanceof FilterPrice)
                        priceList.add((FilterPrice) filter);
                    else if (filter instanceof FilterType)
                        typeList.add((FilterType) filter);
                }

                String val = "{";
                String priceRanges = null;
                if (priceList.size() != 0) {
                    priceRanges = "\"priceRanges\":[";
                    for (int i = 0; i < priceList.size(); i++) {
                        priceRanges += "{";
                        priceRanges += "\"min\":" + priceList.get(i).getMin();
                        priceRanges += ",\"max\":" + priceList.get(i).getMax();
                        priceRanges += "}";
                        if (i != priceList.size() - 1)
                            priceRanges += ",";
                    }
                    priceRanges += "]";
                    Log.d("dj", "getParams - priceRanges: " + priceRanges);
                }

                String prodTypes = null;
                if (typeList.size() != 0) {
                    prodTypes = "\"prodTypes\":[";
                    for (int i = 0; i < typeList.size(); i++) {
                        prodTypes += "\"" + typeList.get(i).getProdType() + "\"";
                        if (i != typeList.size() - 1)
                            prodTypes += ",";
                    }
                    prodTypes += "]";
                    Log.d("dj", "getParams - prodTypes: " + prodTypes);
                }

                String desgnIds = null;
                if (designerList.size() != 0) {
                    desgnIds = "\"desgnIds\":[";
                    for (int i = 0; i < designerList.size(); i++) {
                        desgnIds += designerList.get(i).getDesignerId();
                        if (i != designerList.size() - 1)
                            desgnIds += ",";
                    }
                    desgnIds += "]";
                    Log.d("dj", "getParams - desgnIds: " + desgnIds);
                }

                String collIds = null;
                if (collectionList.size() != 0) {
                    collIds = "\"collIds\":[";
                    for (int i = 0; i < collectionList.size(); i++) {
                        collIds += collectionList.get(i).getCollId();
                        if (i != collectionList.size() - 1)
                            collIds += ",";
                    }
                    collIds += "]";
                    Log.d("dj", "getParams - collIds: " + collIds);
                }
                if (priceRanges != null)
                    val += priceRanges + ",";
                if (prodTypes != null)
                    val += prodTypes + ",";
                if (desgnIds != null)
                    val += desgnIds + ",";
                if (collIds != null)
                    val += collIds + ",";
                val += "\"offset\" : " + offset;
                val += ",\"sort\" : \"" + sort + "\"}";
                Log.d("dj", "getParams - final built string param: " + val);
                /*
                {
                    "priceRanges":
                    [
                    {
                        "min": 1000,
                            "max": 10000
                    },
                    {
                        "min": 6000,
                            "max": 13000
                    }
                    ],
                    "desgnIds": [11,12],
                    "collIds": [13,14,15],
                    "prodTypes": ["Pendants","Rings"],
                    "offset" : 10
                }*/
                return val;
            }
        }
        return null;
    }

    private int offset = 0;

    public class DataManager extends DefaultProjectDataManager {
        public DataManager(Context context, IDataManagerDelegate delegate, List<Cookie> cookies) {
            super(context, delegate, cookies);
            setIsConfigurableObject(true);
        }

        public Map<String, Object> getNextDataServerCallParams(PageData data) {
            return getNextDataParams(data);
        }

        protected boolean isRefreshPage(PageData pageData, String url) {
            return false;
        }

        protected void updatePagingData(BaseDataParser loadedDataVO) {
            if (loadedDataVO != null && loadedDataVO instanceof Result) {
                Result result = (Result) loadedDataVO;
                if (result.offset != -1 && offset != result.offset) {
                    offset = result.offset;
                    pageData.curruntPage += 1;
                    pageData.totalPage += 1;
                } else {
                    offset = result.offset;
                    pageData.totalPage = pageData.curruntPage;
                }
            } else {
                pageData.totalPage = pageData.curruntPage;
            }

        }
    }

    public String getNextDataURL(PageData pageData) {
        if (pageData.curruntPage != pageData.totalPage)
            return getApp().getUrlHelper().getApplyfilterServiceURL();
        return null;
    }

    @Override
    public String getRefreshDataURL(PageData pageData) {
        return null;
    }

    public Class getLoadedDataParsingAwareClass() {
        return Result.class;
    }


    @Override
    public View getItemView(int i, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.product_pick_grid_item, viewGroup, false);
        ViewDataBinding binding = DataBindingUtil.bind(view);
        return binding.getRoot();
    }

    @Override
    public BaseItemHolder getItemHolder(int i, View view) {
        return new BindingItemHolder(view, BR.product);
    }


    public class BindingItemHolder<T extends ProductPickGridItemBinding> extends BindingRecycleItemHolder<T> {
        public BindingItemHolder(View itemView, int variableID) {
            super(itemView, variableID);
        }

        /*@Override
        public void updateItemView(Object data, View view, int position) {
            super.updateItemView(data, view, position);
            //previouslySelected.contains((FilterProductListing) data);
        }*/
    }

    public static class Result extends CodeDataParser {
        List<FilterProductListing> data;
        private int offset = -1;

        public List<?> getList() {
            return data;
        }

        public List<FilterProductListing> getData() {
            return data;
        }

        public void setData(List<FilterProductListing> data) {
            this.data = data;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }


}