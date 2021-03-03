<#include "/include/layout.ftl" />

<@layout "博客分类">

    <#include "/include/header-panel.ftl" />

    <div class="layui-container">
        <div class="layui-row layui-col-space15">
            <div class="layui-col-md8">
                <div class="fly-panel" style="margin-bottom: 0;">

                    <div class="fly-panel-title fly-filter">
                        <a href="/category/recommend?id=${currentCategoryId}&order=${isOrder}"
                           class="<#if !isRecommend>layui-this</#if>">综合</a>
                        <!--<span class="fly-mid"></span>
                        <a href="">未结</a>
                        <span class="fly-mid"></span>
                        <a href="">已结</a>-->
                        <span class="fly-mid"></span>
                        <a href="/category/recommend?id=${currentCategoryId}&recommend=true&order=${isOrder}"
                           class="<#if isRecommend>layui-this</#if>">精贴</a>
                        <span class="fly-filter-right layui-hide-xs">
                            <a href="/category/recommend?id=${currentCategoryId}&recommend=${isRecommend}&order=created"
                               class="<#if isOrder=='created'>layui-this</#if>">按最新</a>
                            <span class="fly-mid"></span>
                            <a href="/category/recommend?id=${currentCategoryId}&recommend=${isRecommend}&order=comment_count"
                               class="<#if isOrder=='comment_count'>layui-this</#if>">按热议</a>
                        </span>
                    </div>

                    <@posts ps=10 cp=cp categoryId=currentCategoryId recommend=isRecommend order=isOrder>
                        <ul class="fly-list">
                            <#list results.records as post>
                                <@postList post></@postList>
                            </#list>
                        </ul>

                        <#if results.total==0>
                            <div class="fly-none">没有相关数据</div>
                        </#if>
                        <@paging results></@paging>
                    </@posts>
                </div>
            </div>

            <#include "/include/right.ftl" />
        </div>
    </div>

    <script>
      layui.cache.page = 'jie';
    </script>

</@layout>
