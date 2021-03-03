<#include "./include/layout.ftl" />

<@layout "首页">

    <#include "./include/header-panel.ftl" />
<#--右侧栏-->
    <div class="layui-container">
        <div class="layui-row layui-col-space15">

            <div class="layui-col-md8">

                <#--内容上面置顶内容-->
                <div class="fly-panel">
                    <div class="fly-panel-title fly-filter">
                        <a>置顶</a>
                        <a href="#signin" class="layui-hide-sm layui-show-xs-block fly-right" id="LAY_goSignin"
                           style="color: #FF5722;">去签到</a>
                    </div>
                    <ul class="fly-list">
                        <@posts ps=3 level=1 >
                            <#list results.records as post>
                                <@postList post></@postList>
                            </#list>

                            <#if results.total==0>
                                <div style="text-align: center;height: 25px;line-height: 25px;padding: 10px">当前暂无置顶贴
                                </div>
                            </#if>
                        </@posts>
                    </ul>
                </div>

                <#--内容-->
                <div class="fly-panel" style="margin-bottom: 0;">
                    <#--内容分类-->
                    <div class="fly-panel-title fly-filter">
                        <a href="/recommend?order=${isOrder}"
                           class="<#if !isRecommend>layui-this</#if>">综合</a>
                        <!--<span class="fly-mid"></span>
                        <a href="">未结</a>
                        <span class="fly-mid"></span>
                        <a href="">已结</a>-->
                        <span class="fly-mid"></span>
                        <a href="/recommend?recommend=true&order=${isOrder}"
                           class="<#if isRecommend>layui-this</#if>">精贴</a>
                        <span class="fly-filter-right layui-hide-xs">
                            <a href="/recommend?recommend=${isRecommend}&order=created"
                               class="<#if isOrder=='created'>layui-this</#if>">按最新</a>
                            <span class="fly-mid"></span>
                            <a href="/recommend?recommend=${isRecommend}&order=comment_count"
                               class="<#if isOrder=='comment_count'>layui-this</#if>">按热议</a>
                        </span>
                    </div>

                    <ul class="fly-list">
                        <#list pageData.records as post>
                            <@postList post></@postList>
                        </#list>
                    </ul>

                    <#if pageData.total==0>
                        <div class="fly-none">没有相关数据</div>
                    </#if>
                    <@paging pageData></@paging>

                </div>
            </div>

            <#include "./include/right.ftl" />
        </div>
    </div>

</@layout>
