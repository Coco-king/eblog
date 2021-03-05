<#include "./include/layout.ftl" />

<@layout "“${q}”的搜索结果">

    <#include "./include/header-panel.ftl" />
<#--右侧栏-->
    <div class="layui-container">
        <div class="layui-row layui-col-space15">

            <div class="layui-col-md8">

                <#--内容上面置顶内容-->
                <div class="fly-panel">
                    <div class="fly-panel-title fly-filter">
                        <a>关键词：“${q}” 的搜索结果 共发现 ${pageData.total} 条记录</a>
                        <#--<a href="#signin" class="layui-hide-sm layui-show-xs-block fly-right" id="LAY_goSignin"
                           style="color: #FF5722;">去签到</a>-->
                    </div>
                    <#--内容分类-->
                    <div class="fly-panel-title fly-filter">
                        <a href="/search?q=${q}&order=${isOrder}"
                           class="<#if !isRecommend>layui-this</#if>">综合</a>
                        <span class="fly-mid"></span>
                        <a href="/search?q=${q}&recommend=true&order=${isOrder}"
                           class="<#if isRecommend>layui-this</#if>">精贴</a>

                        <span class="fly-filter-right layui-hide-xs">
                            <a href="/search?q=${q}&recommend=${isRecommend}&order=created"
                               class="<#if isOrder=='created'>layui-this</#if>">按最新</a>
                            <span class="fly-mid"></span>
                            <a href="/search?q=${q}&recommend=${isRecommend}&order=commentCount"
                               class="<#if isOrder=='commentCount'>layui-this</#if>">按热议</a>
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

                    <div style="text-align: center">
                        <div id="laypage-main">
                        </div>
                        <script>
                          layui.use(['laypage', 'layer'], function () {
                            var laypage = layui.laypage, layer = layui.layer;
                            //完整功能
                            laypage.render({
                              elem: 'laypage-main',
                              count: ${pageData.total},
                              curr: ${pageData.current},
                              limit: ${pageData.size},
                              layout: ['prev', 'page', 'next', 'skip'],
                              jump: function (obj, first) {
                                if (!first) {
                                  location.href = "?q=${q}&cp=" + obj.curr;
                                }
                              }
                            });
                          })
                        </script>
                    </div>
                </div>
            </div>

            <#include "./include/right.ftl" />
        </div>
    </div>

</@layout>
