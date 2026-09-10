import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/catalog_provider.dart';
import 'category_screen.dart';

class AllCategoriesScreen extends StatelessWidget {
  const AllCategoriesScreen({super.key});
  @override
  Widget build(BuildContext c) {
    final provider=c.watch<CatalogProvider>(); final cats=provider.categories;
    if(provider.loading&&cats.isEmpty)return const Center(child:CircularProgressIndicator());
    if(cats.isEmpty)return Center(child:Column(mainAxisSize:MainAxisSize.min,children:[const Icon(Icons.grid_view_rounded,size:52,color:Colors.grey),const SizedBox(height:10),const Text('No categories available'),const SizedBox(height:12),FilledButton.icon(onPressed:provider.load,icon:const Icon(Icons.refresh),label:const Text('Retry'))]));
    return Scaffold(appBar:AppBar(title:const Text('All Categories',style:TextStyle(fontWeight:FontWeight.w900))),body:GridView.builder(padding:const EdgeInsets.all(18),itemCount:cats.length,gridDelegate:const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount:3,crossAxisSpacing:12,mainAxisSpacing:12,childAspectRatio:.86),itemBuilder:(_,i){final x=cats[i];return InkWell(borderRadius:BorderRadius.circular(18),onTap:()=>Navigator.push(c,MaterialPageRoute(builder:(_)=>CategoryScreen(category:x.name))),child:Card(child:Padding(padding:const EdgeInsets.all(10),child:Column(children:[Expanded(child:Image.asset(x.icon,fit:BoxFit.contain,errorBuilder:(_,__,___)=>const Icon(Icons.category_rounded,size:38))),const SizedBox(height:6),Text(x.name,textAlign:TextAlign.center,maxLines:2,overflow:TextOverflow.ellipsis,style:const TextStyle(fontWeight:FontWeight.w700,fontSize:12)),const SizedBox(height:2),Text('${provider.search('',category:x.name).length}+ items',style:const TextStyle(fontSize:10,color:Colors.black54))]))));});
  }
}
